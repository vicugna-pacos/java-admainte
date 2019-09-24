package jp.vicugna_pacos.admainte.main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;

import jp.vicugna_pacos.admainte.bean.InputFileBean;
import jp.vicugna_pacos.admainte.bean.ParameterBean;
import jp.vicugna_pacos.admainte.service.AdService;

/**
 * Active Directory メンテナンス のエントリーポイント
 */
public class AdMainteMain {

	/** ログ */
	private static final Logger log = LoggerFactory.getLogger(AdService.class);

	public static void main(String[] args) {
		int status = 0;
		try {
			log.info("[START]");

			ParameterBean param = getParameter(args);

			List<InputFileBean> inputList = readInputFile(param.getInputFilePath());
			AdService service = new AdService();

			service.update(inputList);

			// エラーリスト出力
			writeErrorFile(param.getErrorFilePath(), service.getErrorList());

		} catch (Exception e) {
			log.error("システムエラーが発生しました", e);
			status = -1;
		}
		log.info("[END] status=" + String.valueOf(status));

		System.exit(status);
	}

	/**
	 * 起動パラメータ取得
	 *
	 * @param args
	 * @return
	 */
	private static ParameterBean getParameter(String[] args) {
		ParameterBean param = new ParameterBean(args);

		if (StringUtils.isEmpty(param.getInputFilePath())) {
			throw new IllegalArgumentException("パラメータに入力ファイルパスが指定されていません。");
		}

		return param;
	}

	/**
	 * 入力ファイルを読み取る
	 *
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	private static List<InputFileBean> readInputFile(String filePath) throws IOException {
		List<InputFileBean> list = null;

		// ファイルを読み取る
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(filePath), "UTF-8"))) {

			CsvToBeanBuilder<InputFileBean> builder = new CsvToBeanBuilder<InputFileBean>(reader);
			builder.withSeparator('\t');
			builder.withType(InputFileBean.class);

			list = builder.build().parse();
		}

		Collections.sort(list);

		return list;
	}

	/**
	 * エラーファイル書込み
	 *
	 * @param filePath エラーファイル
	 * @param errorList
	 * @throws IOException
	 */
	private static void writeErrorFile(String filePath, List<InputFileBean> errorList) throws IOException {
		if (StringUtils.isEmpty(filePath)) {
			return;
		}

		if (errorList == null || errorList.size() > 0) {
			return;
		}

		CSVWriterBuilder builder = new CSVWriterBuilder(
				new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));
		builder.withSeparator('\t'); // タブ区切り
		builder.withLineEnd("\r\n"); // 改行コード(デフォルトだと\nになる)

		try (ICSVWriter writer = builder.build()) {

			// ヘッダーの出力
			String[] header = new String[] { "userId", "name", "displayName", "givenName", "sn", "mail", "pager", "department" };
			writer.writeNext(header, false);

			for (InputFileBean bean : errorList) {
				// 内容出力
				List<String> list = new ArrayList<String>();
				list.add(bean.getUserId());
				list.add(bean.getName());
				list.add(bean.getDisplayName());
				list.add(bean.getGivenName());
				list.add(bean.getSn());
				list.add(bean.getMail());
				list.add(bean.getPager());
				list.add(bean.getDepartment());

				writer.writeNext(list.toArray(new String[] {}), false);
			}
		}
	}

}
