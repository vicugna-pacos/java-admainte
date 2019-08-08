package jp.vicugna_pacos.admainte.bean;

import com.opencsv.bean.CsvBindByName;

/**
 * 入力ファイルBean
 */
public class InputFileBean implements Comparable<InputFileBean> {

	/** 社員番号 */
	@CsvBindByName(required = true)
	private String userId = null;

	@CsvBindByName(required = false)
	private String name = null;

	/** 名前(姓名) */
	@CsvBindByName(required = false)
	private String displayName = null;

	/** ファーストネーム */
	@CsvBindByName(required = false)
	private String givenName = null;

	/** 苗字 */
	@CsvBindByName(required = false)
	private String sn = null;

	/** メールアドレス */
	@CsvBindByName(required = false)
	private String mail = null;

	/** ポケットベル */
	@CsvBindByName(required = false)
	private String pager = null;

	/** ポケットベル(その他) */
	@CsvBindByName(required = false)
	private String otherPager = null;

	@Override
	public int compareTo(InputFileBean o) {
		int compare = 0;

		if (userId == null) {
			if (o.getUserId() != null) {
				return -1;
			}

		} else {
			compare = userId.compareTo(o.getUserId());

			if (compare != 0) {
				return compare;
			}
		}

		if (otherPager == null) {
			if (o.getOtherPager() != null) {
				return -1;
			}

		} else {
			compare = otherPager.compareTo(o.getOtherPager());

			if (compare != 0) {
				return compare;
			}
		}

		return 0;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InputFileBean [userId=");
		builder.append(userId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", displayName=");
		builder.append(displayName);
		builder.append(", givenName=");
		builder.append(givenName);
		builder.append(", sn=");
		builder.append(sn);
		builder.append(", mail=");
		builder.append(mail);
		builder.append(", pager=");
		builder.append(pager);
		builder.append(", otherPager=");
		builder.append(otherPager);
		builder.append("]");
		return builder.toString();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPager() {
		return pager;
	}

	public void setPager(String pager) {
		this.pager = pager;
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

	public String getOtherPager() {
		return otherPager;
	}

	public void setOtherPager(String otherPager) {
		this.otherPager = otherPager;
	}

}
