package jp.vicugna_pacos.admainte.bean;

/**
 * 起動パラメータ
 */
public class ParameterBean {

	/** 入力ファイルパス */
	private String inputFilePath = null;

	/** エラーファイルパス */
	private String errorFilePath = null;

	/**
	 * コンストラクタ
	 */
	public ParameterBean() {
	}

	/**
	 * コンストラクタ
	 *
	 * @param args
	 */
	public ParameterBean(String[] args) {
		String key = null;

		for (int i = 0; i < args.length; i++) {
			// キーの判定
			if ("-input".equals(args[i])) {
				key = args[i];
				continue;

			} else if ("-error".equals(args[i])) {
				key = args[i];
				continue;
			}

			// 値取得
			if ("-input".equals(key)) {
				inputFilePath = args[i];
				key = null;

			} else if ("-error".equals(key)) {
				errorFilePath = args[i];
				key = null;
			}

		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParameterBean [inputFilePath=");
		builder.append(inputFilePath);
		builder.append(", errorFilePath=");
		builder.append(errorFilePath);
		builder.append("]");
		return builder.toString();
	}

	public String getInputFilePath() {
		return inputFilePath;
	}

	public void setInputFilePath(String inputFilePath) {
		this.inputFilePath = inputFilePath;
	}

	public String getErrorFilePath() {
		return errorFilePath;
	}

	public void setErrorFilePath(String errorFilePath) {
		this.errorFilePath = errorFilePath;
	}

}
