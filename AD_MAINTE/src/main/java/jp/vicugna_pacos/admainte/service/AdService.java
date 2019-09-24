package jp.vicugna_pacos.admainte.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vicugna_pacos.admainte.bean.AdUserBean;
import jp.vicugna_pacos.admainte.bean.InputFileBean;

/**
 * Active Directoryを参照/更新するService
 */
public class AdService {

	/** プロパティファイルの内容 */
	private final ResourceBundle config = ResourceBundle.getBundle("config");

	/** ログ */
	private final Logger log = LoggerFactory.getLogger(AdService.class);

	/** 更新失敗データ */
	private List<InputFileBean> errorList = null;

	/**
	 * ユーザー属性の一括更新
	 *
	 * @param inputList 入力ファイル
	 * @throws NamingException
	 * @throws IOException
	 */
	public void update(List<InputFileBean> inputList) throws NamingException, IOException {
		errorList = new ArrayList<InputFileBean>();

		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, config.getString("ldap.url"));
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, config.getString("ldap.principal"));
		env.put(Context.SECURITY_CREDENTIALS, config.getString("ldap.password"));

		InitialDirContext context = null;
		try {
			context = new InitialDirContext(env);

			log.info("--- AD更新 ---");

			int size = inputList.size();
			int index = 0;
			InputFileBean bean = null;

			if (index < size) {
				bean = inputList.get(index);
			}

			while (index < size) {
				InputFileBean key = bean;
				boolean skip = false;

				// ADからユーザー情報取得
				AdUserBean adUser = getAdUser(context, key.getUserId());

				if (adUser == null || StringUtils.isEmpty(adUser.getDistinguishedName())) {
					log.warn("識別名が取得できなかったため、更新をスキップします。userId=" + key.getUserId());
					errorList.add(key);
					skip = true;
				}

				List<InputFileBean> inputListUser = new ArrayList<>();

				// ユーザーIDが同じ間、ループを続ける
				while (index < size && key.getUserId().equals(bean.getUserId())) {

					inputListUser.add(bean);

					index++;
					if (index < size) {
						bean = inputList.get(index);
					}
				}
				// ループの終わり

				// 更新
				if (!skip) {
					updatePerUser(context, adUser.getDistinguishedName(), inputListUser);
				}

			}

		} finally {
			try {
				if (context != null) {
					context.close();
				}
			} catch (NamingException e) {
			}
		}

	}

	/**
	 * AD更新
	 *
	 * @param context
	 * @param distinguishedName
	 * @param inputList
	 */
	private void updatePerUser(InitialDirContext context, String distinguishedName, List<InputFileBean> inputList) {

		if (inputList.size() == 0) {
			return;
		}

		BasicAttributes attrs = new BasicAttributes();

		// 単一項目の取得
		InputFileBean first = inputList.get(0);

		addSingleAttribute(attrs, "mail", first.getMail());
		addSingleAttribute(attrs, "pager", first.getPager());
		addSingleAttribute(attrs, "displayName", first.getDisplayName());
		addSingleAttribute(attrs, "givenName", first.getGivenName());
		addSingleAttribute(attrs, "sn", first.getSn());
		addSingleAttribute(attrs, "department", first.getDepartment());

		// 複数項目の取得
		for (InputFileBean input : inputList) {
			addMultipleAttribute(attrs, "otherPager", input.getOtherPager());
		}

		if (attrs.size() == 0) {
			log.warn("更新対象の値が設定されていないため、スキップします。userId=" + first.getUserId());
			errorList.addAll(inputList);
			return;
		}

		// 更新実行
		String name = distinguishedName;

		try {
			context.modifyAttributes(name, DirContext.REPLACE_ATTRIBUTE, attrs);
		} catch (NamingException e) {
			log.error("更新に失敗しました。" + name + ", message=" + e.getLocalizedMessage());
			errorList.addAll(inputList);
			return;
		}

	}

	/**
	 * 単一項目の属性値をBasicAttributesに追加する
	 *
	 * @param attrs 更新値になるBasicAttributes
	 * @param id
	 * @param value
	 */
	private void addSingleAttribute(BasicAttributes attrs, String id, String value) {
		if (StringUtils.isNotEmpty(value)) {
			attrs.put(new BasicAttribute(id, value));
		}

	}

	/**
	 * 複数項目の属性値をBasicAttributesに追加する
	 *
	 * @param attrs 更新値になるBasicAttributes
	 * @param id
	 * @param value
	 */
	private void addMultipleAttribute(BasicAttributes attrs, String id, String value) {
		if (StringUtils.isEmpty(value)) {
			return;
		}

		Attribute attr = attrs.get(id);

		if (attr == null) {
			attr = new BasicAttribute(id, value);
			attrs.put(attr);

		} else {
			attr.add(value);
		}
	}

	/**
	 * ユーザーIDをキーとしてADを検索し、ユーザー情報を取得する。
	 *
	 * 結果が0件または2件以上の場合はログを出力し、nullを返す。
	 *
	 * @param context
	 * @param userId
	 * @return
	 * @throws NamingException
	 */
	private AdUserBean getAdUser(DirContext context, String userId) throws NamingException {

		String name = config.getString("ldap.get_user.name");
		String filter = config.getString("ldap.get_user.filter");

		filter = filter.replace("[userId]", userId);

		SearchControls control = new SearchControls();
		control.setSearchScope(SearchControls.SUBTREE_SCOPE);

		NamingEnumeration<SearchResult> result = context.search(name, filter, control);

		if (result.hasMoreElements()) {
			AdUserBean adUser = new AdUserBean(result.nextElement());

			if (result.hasMoreElements()) {
				// 結果が2件以上あるのでエラー
				log.warn("結果が2件以上あります。name=" + name + ", filter=" + filter);
				return null;
			}

			log.debug(adUser.toString());

			return adUser;

		}

		// 該当なし
		log.warn("ActiveDirectoryに存在しないユーザーです。name=" + name + ", filter=" + filter);
		return null;

	}

	public List<InputFileBean> getErrorList() {
		return errorList;
	}

}
