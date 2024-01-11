package my.practice.user.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

import io.micrometer.core.instrument.util.IOUtils;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.junit.jupiter.api.Test;

public class CompressTest {

	private static final String json = "{\"id\":9001,\"loginId\":\"D209001\",\"name\":\"관리자\",\"company\":{\"id\":21,\"" +
			"companyName\":\"KBDS\",\"companyType\":\"CUSTOMER\"},\"department\":null,\"position\":null,\"roleGroup" +
			"Id\":10000,\"status\":\"ACTIVE\",\"lastPasswordModifyDate\":\"2023-11-28 17:29\",\"lastLoginDate\":\"2" +
			"024-01-11 16:06\",\"regDate\":\"2023-11-28 17:29\",\"useOfServiceYn\":\"Y\",\"useOfServiceDate\":\"202" +
			"3-11-28 17:29\"}";
	private static final String json2 = "{\"id\":12952,\"loginId\":\"D220540\",\"name\":\"이전도\",\"company\":{\"id\":21,\"companyName\":\"KBDS\",\"companyType\":\"CUSTOMER\"},\"department\":\"SaaS사업부\",\"position\":\"사원\",\"roleGroupId\":10050,\"status\":\"ACTIVE\",\"lastPasswordModifyDate\":\"2024-01-04 12:04\",\"lastLoginDate\":\"2024-01-11 14:58\",\"regDate\":\"2024-01-04 12:04\",\"useOfServiceYn\":\"N\",\"useOfServiceDate\":null,\"roleGroup\":{\"id\":10050,\"name\":\"일반유저\",\"type\":\"USER\",\"menus\":[{\"id\":10004,\"menuId\":11954,\"name\":\"SLA 보고서\",\"parentId\":null,\"level\":1,\"url\":null,\"subMenus\":[{\"id\":10000,\"menuId\":11961,\"name\":\"보고서 관리\",\"parentId\":11954,\"level\":2,\"url\":\"/sla/reports/management\",\"subMenus\":[]},{\"id\":10003,\"menuId\":11960,\"name\":\"SLA 보고서\",\"parentId\":11954,\"level\":2,\"url\":\"/sla/reports\",\"subMenus\":[]}]},{\"id\":10007,\"menuId\":11952,\"name\":\"클라우드 관리\",\"parentId\":null,\"level\":1,\"url\":null,\"subMenus\":[{\"id\":10006,\"menuId\":11953,\"name\":\"인프라 자원 현황\",\"parentId\":11952,\"level\":2,\"url\":null,\"subMenus\":[]}]},{\"id\":10008,\"menuId\":11955,\"name\":\"이용내역서\",\"parentId\":null,\"level\":1,\"url\":null,\"subMenus\":[{\"id\":10001,\"menuId\":11962,\"name\":\"이용내역서\",\"parentId\":11955,\"level\":2,\"url\":\"/usage\",\"subMenus\":[]},{\"id\":10002,\"menuId\":11964,\"name\":\"FCC콜 인프라\",\"parentId\":11955,\"level\":2,\"url\":\"/usage/fcc\",\"subMenus\":[{\"id\":10010,\"menuId\":11966,\"name\":\"비용 현황 분석\",\"parentId\":11964,\"level\":3,\"url\":\"/usage/fcc/cost\",\"subMenus\":[]},{\"id\":10011,\"menuId\":11967,\"name\":\"사용량 현황 조회\",\"parentId\":11964,\"level\":3,\"url\":\"/usage/fcc/report\",\"subMenus\":[]}]}]},{\"id\":10009,\"menuId\":11956,\"name\":\"빌링 관리\",\"parentId\":null,\"level\":1,\"url\":null,\"subMenus\":[]}],\"items\":[],\"inUse\":true,\"createdBy\":\"관리자\",\"createdDate\":\"2024-01-04 02:56:30\",\"lastModifiedBy\":\"관리자\",\"lastModifiedDate\":\"2024-01-04 12:02:06\"},\"admin\":false,\"companyAdmin\":false}";

	@Test
	void apacheCompressTest() throws Exception {
		Compress apache = new ApacheCompress();
		test("Apache Compress", apache, json);
		test("Apache Compress", apache, json2);
		Compress deflater = new DeflaterCompress();
		test("Deflater Compress", deflater, json);
		test("Deflater Compress", deflater, json2);
	}

	private void test(String compressName, Compress compress, String str) throws Exception {
		System.out.printf("%n=========== %s ===========%n", compressName);

		Base64.Encoder encoder = Base64.getEncoder();
		System.out.println("압축 전 길이: " + str.getBytes().length);
		long start = System.currentTimeMillis();

		byte[] encode = null;
		for (int i=0 ; i<100 ; i++) {
			encode = encoder.encode(compress.compressString(str));
		}
		System.out.println("압축 후 길이: " + encode.length);
//		System.out.println("압축 문자열: " + new String(encode));
		long compressTime = System.currentTimeMillis() - start;
		start = System.currentTimeMillis();
		System.out.println("압축 걸린 시간: " + compressTime);

		byte[] decode = null;
		for (int i=0 ; i<100 ; i++) {
			decode = compress.decompressBytes(Base64.getDecoder().decode(encode));
		}
		System.out.println("압축 푼 후 길이: " + decode.length);
		long deCompressTime = System.currentTimeMillis() - start;
		System.out.println("압축 푸는데 걸린 시간: " + deCompressTime);
	}


	interface Compress {
		byte[] compressString(String input) throws Exception;
		byte[] decompressBytes(byte[] compressedBytes) throws Exception;
	}

	static class ApacheCompress implements Compress {
		public byte[] compressString(String input) throws Exception {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			try (CompressorOutputStream compressorOutputStream = new CompressorStreamFactory()
					.createCompressorOutputStream(CompressorStreamFactory.GZIP, outputStream)) {
				compressorOutputStream.write(input.getBytes(StandardCharsets.UTF_8));
			}
			return outputStream.toByteArray();
		}

		public byte[] decompressBytes(byte[] compressedBytes) throws Exception {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(compressedBytes);
			try (CompressorInputStream compressorInputStream = new CompressorStreamFactory()
					.createCompressorInputStream(CompressorStreamFactory.GZIP, inputStream)) {
				return IOUtils.toString(compressorInputStream, StandardCharsets.UTF_8).getBytes();
			}
		}
	}

	static class DeflaterCompress implements Compress {
		public byte[] compressString(String input) throws Exception {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try (DeflaterOutputStream dos = new DeflaterOutputStream(os)) {
				dos.write(input.getBytes());
			}
			return os.toByteArray();
		}

		public byte[] decompressBytes(byte[] compressedBytes) throws Exception {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try (OutputStream ios = new InflaterOutputStream(os)) {
				ios.write(compressedBytes);
			}
			return os.toByteArray();
		}
	}

}
