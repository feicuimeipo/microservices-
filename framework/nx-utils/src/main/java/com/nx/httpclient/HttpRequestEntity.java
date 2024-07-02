package com.nx.httpclient;



import com.nx.common.context.CurrentRuntimeContext;
import com.nx.common.context.constant.NxRequestHeaders;
import com.nx.common.context.spi.LoginUser;
import com.nx.common.crypt.Base64;
import com.nx.common.exception.BaseException;
import com.nx.common.tracing.NxTraceUtil;
import com.nx.httpclient.adapter.HttpClientProvider;
import com.nx.utils.BeanUtils;
import com.nx.utils.HttpClientJsonUtil;
import com.nx.utils.WebUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import static com.nx.common.context.constant.NxRequestHeaders.HEADER_AUTH_USER;


/**
 * 
 * 在原做者的在础上做了一些改造
 */
public class HttpRequestEntity {
	public static final String DOT= ".";

	private String uri;
	private HttpMethod method;
	private String charset;
	private String contentType;
	private Map<String, String> headers;
	private Map<String, Object> queryParams;
	private Map<String, Object> formParams;
	private String body;
	private BasicAuthParams basicAuth;

	private boolean multipart;
	private String boundary;

	private HttpRequestEntity() {
	}

	public static HttpRequestEntity create(HttpMethod method) {
		return new HttpRequestEntity().method(method);
	}

	public static HttpRequestEntity post(String uri) {
		return new HttpRequestEntity().method(HttpMethod.POST).uri(uri);
	}

	public static HttpRequestEntity get(String uri) {
		return new HttpRequestEntity().method(HttpMethod.GET).uri(uri);
	}
	
	public HttpRequestEntity internalCall() {
		if (!getHeaders().containsKey(NxTraceUtil.TRACE_ID_NAME)) {
			header(NxTraceUtil.TRACE_ID_NAME, NxTraceUtil.generateTraceId());
		}
		return header(WebUtil.HEADER_INTERNAL_REQUEST, Boolean.TRUE.toString());
	}

	public HttpRequestEntity backendInternalCall() {
		return internalCall();
	}

	public HttpRequestEntity useContext() {
		Map<String, String> contextHeaders = CurrentRuntimeContext.getContextHeaders();
		headers(contextHeaders);
		if (!getHeaders().containsKey(NxTraceUtil.TRACE_ID_NAME)) {
			header(NxTraceUtil.TRACE_ID_NAME, NxTraceUtil.generateTraceId());
		}
		LoginUser currentUser;
		if (!headers.containsKey(HEADER_AUTH_USER)
				&& (currentUser = CurrentRuntimeContext.getCurrentUser()) != null) {
			header(HEADER_AUTH_USER, currentUser.encode());
		}
		
		return this;
	}

	public String getCharset() {
		if (charset == null) {
			charset = parseContentTypeCharset(contentType);
		}
		return charset;
	}

	public HttpRequestEntity charset(String charset) {
		this.charset = charset;
		return this;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public String getUri() {
		return uri;
	}

	public HttpRequestEntity uri(String uri) {
		this.uri = uri;
		return this;
	}

	public HttpRequestEntity method(HttpMethod method) {
		this.method = method;
		return this;
	}

	public String getContentType() {
		if (StringUtils.isBlank(contentType)) {
			return HttpClientProvider.CONTENT_TYPE_JSON_UTF8;
		}
		return contentType;
	}

	public HttpRequestEntity contentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

	public boolean isMultipart() {
		return multipart;
	}

	public Map<String, String> getHeaders() {
		return headers == null ? (headers = new LinkedHashMap<>()) : headers;
	}

	public HttpRequestEntity headers(Map<String, String> headers) {
		this.headers = headers;
		return this;
	}

	public HttpRequestEntity header(String name, String value) {
		this.getHeaders().put(name, value);
		return this;
	}

	public Map<String, Object> getQueryParams() {
		return queryParams;
	}

	public HttpRequestEntity queryParams(Map<String, Object> queryParams) {
		this.queryParams = queryParams;
		return this;
	}

	public HttpRequestEntity queryParam(String name, Object value) {
		if (this.queryParams == null)
			this.queryParams = new HashMap<>(3);
		this.queryParams.put(name, value);
		return this;
	}

	public Map<String, Object> getFormParams() {
		return formParams;
	}

	public HttpRequestEntity fileParam(String name, File file) {
		if (this.formParams == null)
			this.formParams = new HashMap<>();
		this.formParams.put(name, new FileItem(file));
		if (contentType == null) {
			contentType = HttpClientProvider.CONTENT_TYPE_FROM_MULTIPART_UTF8;
		}
		if (!multipart) {
			multipart = true;
			boundary = String.valueOf(System.nanoTime()); // 随机分隔线
			contentType = contentType + ";boundary=" + boundary;
		}
		return this;
	}

	public HttpRequestEntity fileParam(String name, String originalFilename, InputStream inputStream, String mimeType,
			long size) {
		if (this.formParams == null)
			this.formParams = new HashMap<>();
		this.formParams.put(name, new FileItem(originalFilename, inputStream, mimeType, size));
		if (contentType == null) {
			contentType = HttpClientProvider.CONTENT_TYPE_FROM_MULTIPART_UTF8;
		}
		if (!multipart) {
			multipart = true;
			boundary = String.valueOf(System.nanoTime()); // 随机分隔线
			contentType = contentType + ";boundary=" + boundary;
		}
		return this;
	}

	public HttpRequestEntity fileParam(String name, String originalFilename, byte[] data, String mimeType) {
		if (this.formParams == null)
			this.formParams = new HashMap<>();
		this.formParams.put(name, new FileItem(originalFilename, data, mimeType));
		if (contentType == null) {
			contentType = HttpClientProvider.CONTENT_TYPE_FROM_MULTIPART_UTF8;
		}
		if (!multipart) {
			multipart = true;
			boundary = String.valueOf(System.nanoTime()); // 随机分隔线
			contentType = contentType + ";boundary=" + boundary;
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public HttpRequestEntity formParams(Object formdata) {
		if (formdata instanceof Map) {
			this.formParams = (Map<String, Object>) formdata;
		} else {
			this.formParams = BeanUtils.beanToMap(formdata);
		}
		return this;
	}

	public HttpRequestEntity formParam(String name, String value) {
		if (this.formParams == null)
			this.formParams = new HashMap<>();
		this.formParams.put(name, value);
		if (contentType == null) {
			contentType = HttpClientProvider.CONTENT_TYPE_FROM_URLENCODED_UTF8;
		}
		return this;
	}

	public String getBody() {
		return body;
	}

	public HttpRequestEntity body(Object body) {
		if (method != HttpMethod.POST || body == null) {
			return null;
		}
		if (body instanceof String) {
			this.body = body.toString();
		} else {
			this.body = HttpClientJsonUtil.toJson(body);
		}

		return this;
	}

	public String getBoundary() {
		return boundary;
	}

	public BasicAuthParams getBasicAuth() {
		return basicAuth;
	}

	public HttpRequestEntity basicAuth(String userName, String password) {
		this.basicAuth = new BasicAuthParams(userName, password);
		return this;
	}

	public static String parseContentTypeCharset(String contentType) {
		String charset = HttpClientProvider.CHARSET_UTF8;
		if (StringUtils.isBlank(contentType))
			return charset;
		String[] params = StringUtils.split(contentType, ";");
		for (String param : params) {
			param = param.trim();
			if (param.toLowerCase().startsWith("charset")) {
				String[] pair = param.split("=", 2);
				if (pair.length == 2) {
					if (!StringUtils.isEmpty(pair[1])) {
						charset = pair[1].trim();
					}
				}
				break;
			}
		}

		return charset;
	}
	
	public StringBuilder buildRequestLog() {
		//后台异步任务不打印
		if(!Thread.currentThread().getName().contains("http-")) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		builder.append("\n---------------backend request trace start--------------------");
		builder.append("\nurl:").append(uri);
		if(getHeaders().containsKey(NxRequestHeaders.HEADER_SYSTEM_ID) || getHeaders().containsKey(NxRequestHeaders.HEADER_TENANT_ID)) {
			builder.append("\nheaders");
			builder.append("\n - x-system-id:").append(getHeaders().get(NxRequestHeaders.HEADER_SYSTEM_ID));
			builder.append("\n - x-tenant-id:").append(getHeaders().get(NxRequestHeaders.HEADER_TENANT_ID));
		}
		if(getQueryParams() != null && !getQueryParams().isEmpty()) {
			builder.append("\nqueryParams:").append(getQueryParams());
		}
		if(getBody() != null) {
			builder.append("\nbody:").append(getBody());
		}
		return builder;
	}

	public HttpResponseEntity execute() {
		return NxHttpUtil.execute(this);
	}

	public void unset() {
		if (!isMultipart())
			return;
		Object entryValue;
		for (Entry<String, Object> entry : formParams.entrySet()) {
			entryValue = entry.getValue();
			if (entryValue == null)
				continue;
			if (entryValue instanceof FileItem) {
				FileItem fileItem = (FileItem) entryValue;
				if (fileItem.content != null) {
					fileItem.content = null;
				} else if (fileItem.createdTempFile) {
					try {
						FileUtils.forceDelete(fileItem.file);
						fileItem.createdTempFile = false;
					} catch (Exception e) {
					}
				}
			}
		}
	}

	public static class BasicAuthParams {
		private String name;
		private String password;

		public BasicAuthParams(String name, String password) {
			super();
			this.name = name;
			this.password = password;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getEncodeBasicAuth() {
			String encoded = Base64.encodeToString((name + ":" + password).getBytes(StandardCharsets.UTF_8), false);
			return "Basic " + encoded;
		}

	}

	/**
	 * 文件元数据。
	 */
	public static class FileItem {
		// 每块限制大小，超过的要分块上传
		private static int chunkSizeLimit = HttpClientConfig.getFileUploadChunkSizeLimit();
		private static String tmpDir = System.getProperty("java.io.tmpdir") + File.separator + WebUtil.CUSTOMER_PROPERTY_PREFIX;
		static {
			File dir = new File(tmpDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
		}
		private String fileName;
		private String mimeType;
		private byte[] content;
		private File file;
		private InputStream inputStream;
		private long size;

		private int chunkNum = 1;

		private long offset;
		private boolean createdTempFile;

		/**
		 * 基于本地文件的构造器。
		 * 
		 * @param file
		 *            本地文件
		 */
		public FileItem(File file) {
			this.file = file;
			setFileName(file.getName());
			setSize(file.length());
		}

		/**
		 * 基于文件绝对路径的构造器。
		 * 
		 * @param filePath
		 *            文件绝对路径
		 */
		public FileItem(String filePath) {
			this(new File(filePath));
		}

		/**
		 * 基于文件名和字节流的构造器。
		 * 
		 * @param fileName
		 *            文件名
		 * @param content
		 *            文件字节流
		 */
		public FileItem(String fileName, byte[] content) {
			this.content = content;
			this.size = content.length;
			setFileName(fileName);
		}

		/**
		 * 基于文件名、字节流和媒体类型的构造器。
		 * 
		 * @param fileName
		 *            文件名
		 * @param content
		 *            文件字节流
		 * @param mimeType
		 *            媒体类型
		 */
		public FileItem(String fileName, byte[] content, String mimeType) {
			this(fileName, content);
			if (mimeType != null) {
				this.mimeType = mimeType;
			}
		}

		public FileItem(String originalFilename, InputStream inputStream, String mimeType, long size) {
			setFileName(originalFilename);
			if (mimeType != null) {
				this.mimeType = mimeType;
			}
			setSize(size);
			// 写入临时文件
			if (chunkNum > 1) {
				String ext = null;
				if (originalFilename.contains(DOT)) {
					ext = originalFilename.substring(originalFilename.lastIndexOf(DOT));
				} else {
					ext = DOT + StringUtils.defaultIfBlank(MimeTypeUtil.getFileExtension(mimeType), "tmp");
				}

				this.file = new File(tmpDir, UUID.randomUUID().toString() + ext);

				FileOutputStream outputStream = null;
				try {
					outputStream = new FileOutputStream(file);
					IOUtils.copy(inputStream, outputStream);
					createdTempFile = true;
				} catch (Exception e) {
					throw new BaseException("写入临时文件错误:" + e.getMessage());
				} finally {
					try {
						if (outputStream != null)
							outputStream.close();
					} catch (Exception e2) {
					}
				}
			} else {
				this.inputStream = inputStream;
			}
		}

		public String getFileName() {
			return this.fileName;
		}

		public String getMimeType() throws IOException {
			if (this.mimeType == null) {
				MimeTypeUtil.FileMeta fileMeta = MimeTypeUtil.getFileMeta(getContent());
				if (fileMeta != null) {
					return fileMeta.getMimeType();
				}
			}
			return this.mimeType;
		}

		public InputStream getInputStream() {
			return inputStream;
		}

		public long getSize() {
			return size;
		}

		public int getChunkNum() {
			return chunkNum;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
			if (fileName.contains(DOT)) {
				String extension = fileName.substring(fileName.lastIndexOf(DOT) + 1).toLowerCase();
				this.mimeType = MimeTypeUtil.getFileMimeType(extension);
			}
		}

		private void setSize(long size) {
			this.size = size;
			if (chunkSizeLimit > 0 && size > chunkSizeLimit) {
				chunkNum = (int) (size / chunkSizeLimit);
				if ((size / chunkSizeLimit) > 0) {
					chunkNum = chunkNum + 1;
				}
			}
		}

		public byte[] getContent() throws IOException {
			if (chunkNum > 1) {
				if (offset >= size)
					return new byte[0];
				byte[] data = getFileChunk(file, offset, chunkSizeLimit);
				offset = offset + chunkSizeLimit;
				if (offset > size)
					offset = size;
				return data;
			}
			if (this.content != null)
				return this.content;
			if (inputStream == null && this.file != null && this.file.exists()) {
				inputStream = new FileInputStream(this.file);
			}
			if (inputStream == null)
				return null;
			ByteArrayOutputStream out = null;

			try {
				out = new ByteArrayOutputStream();
				int ch;
				while ((ch = inputStream.read()) != -1) {
					out.write(ch);
				}
				this.content = out.toByteArray();
			} finally {
				if (out != null) {
					out.close();
				}
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
			}
			return this.content;
		}

		/**
		 * 文件分块工具
		 * 
		 * @param file
		 *            文件
		 * @param offset
		 *            起始偏移位置
		 * @param chunkSize
		 *            分块大小
		 * @return 分块数据
		 */
		private static byte[] getFileChunk(File file, long offset, int chunkSize) {

			byte[] result = new byte[chunkSize];
			RandomAccessFile accessFile = null;
			try {
				accessFile = new RandomAccessFile(file, "r");
				accessFile.seek(offset);
				int readSize = accessFile.read(result);
				if (readSize == -1) {
					return null;
				} else if (readSize == chunkSize) {
					return result;
				} else {
					byte[] tmpByte = new byte[readSize];
					System.arraycopy(result, 0, tmpByte, 0, readSize);
					return tmpByte;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (accessFile != null) {
					try {
						accessFile.close();
					} catch (IOException e1) {
					}
				}
			}
			return null;
		}

	}
}
