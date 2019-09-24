package jp.vicugna_pacos.admainte.bean;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchResult;

/**
 * Active Directoryのユーザー情報
 */
public class AdUserBean {

	private String cn = null;

	private String name = null;
	/** 名前(姓名) */
	private String displayName = null;
	/** ファーストネーム */
	private String givenName = null;
	/** 苗字 */
	private String sn = null;
	/** ユーザーID＋ドメイン */
	private String userPrincipalName = null;
	/** LDAP識別名 */
	private String distinguishedName = null;
	/** メールアドレス */
	private String mail = null;
	/** ポケットベル */
	private String pager = null;
	/** ポケットベル(その他) */
	private List<String> otherPager = null;
	/** 所属 */
	private String department = null;

	/**
	 * コンストラクタ
	 */
	public AdUserBean() {

	}

	/**
	 * コンストラクタ
	 *
	 * Active Directory検索結果から内容を取得する
	 *
	 * @param result
	 * @throws NamingException
	 */
	public AdUserBean(SearchResult result) throws NamingException {
		NamingEnumeration<? extends Attribute> attributes = result.getAttributes().getAll();

		while (attributes.hasMoreElements()) {
			Attribute attribute = attributes.next();
			Object value = attribute.get();

			if ("name".equals(attribute.getID())) {
				if (value instanceof String) {
					name = (String) value;
				}

			} else if ("displayName".equals(attribute.getID())) {
				if (value instanceof String) {
					displayName = (String) value;
				}

			} else if ("givenName".equals(attribute.getID())) {
				if (value instanceof String) {
					givenName = (String) value;
				}

			} else if ("sn".equals(attribute.getID())) {
				if (value instanceof String) {
					sn = (String) value;
				}

			} else if ("cn".equals(attribute.getID())) {
				if (value instanceof String) {
					cn = (String) value;
				}

			} else if ("userPrincipalName".equals(attribute.getID())) {
				if (value instanceof String) {
					userPrincipalName = (String) value;
				}

			} else if ("pager".equals(attribute.getID())) {
				// ポケットベル
				if (value instanceof String) {
					pager = (String) value;
				}

			} else if ("otherPager".equals(attribute.getID())) {
				// ポケットベル(その他)
				NamingEnumeration<?> allValue = attribute.getAll();

				while (allValue.hasMore()) {
					Object obj = allValue.next();
					if (obj instanceof String) {
						if (otherPager == null) {
							otherPager = new ArrayList<String>();
						}
						otherPager.add((String) obj);
					}
				}

			} else if ("mail".equals(attribute.getID())) {
				// メール
				if (value instanceof String) {
					mail = (String) value;
				}

			} else if ("distinguishedName".equals(attribute.getID())) {
				// LDAP識別名
				if (value instanceof String) {
					distinguishedName = (String) value;
				}

			} else if ("department".equals(attribute.getID())) {
				// 所属
				if (value instanceof String) {
					department = (String) value;
				}

			}
		}

	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AdUserBean [cn=");
		builder.append(cn);
		builder.append(", name=");
		builder.append(name);
		builder.append(", displayName=");
		builder.append(displayName);
		builder.append(", givenName=");
		builder.append(givenName);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", userPrincipalName=");
		builder.append(userPrincipalName);
		builder.append(", distinguishedName=");
		builder.append(distinguishedName);
		builder.append(", mail=");
		builder.append(mail);
		builder.append(", pager=");
		builder.append(pager);
		builder.append(", otherPager=");
		builder.append(otherPager);
		builder.append(", department=");
		builder.append(department);
		builder.append("]");
		return builder.toString();
	}

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public String getCn() {
		return cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getUserPrincipalName() {
		return userPrincipalName;
	}

	public void setUserPrincipalName(String userPrincipalName) {
		this.userPrincipalName = userPrincipalName;
	}

	public String getPager() {
		return pager;
	}

	public void setPager(String pager) {
		this.pager = pager;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public List<String> getOtherPager() {
		return otherPager;
	}

	public void setOtherPager(List<String> otherPager) {
		this.otherPager = otherPager;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

}
