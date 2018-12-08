package com.deaking.wallet.core.util;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonrpc4j.IJsonRpcClient;
import com.googlecode.jsonrpc4j.JsonRpcClient;
import org.apache.http.HttpException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class JsonRpcHttpClient extends JsonRpcClient implements IJsonRpcClient {
    private static final String GZIP = "gzip";
    private final Map<String, String> headers;
    private URL serviceUrl;
    private URL noAuthURL;
    private String authStr;
    private Proxy connectionProxy;
    private int connectionTimeoutMillis;
    private int readTimeoutMillis;
    private SSLContext sslContext;
    private HostnameVerifier hostNameVerifier;
    private String contentType;
    private boolean gzipRequests;

    public JsonRpcHttpClient(URL serviceUrl, Map<String, String> headers) {
        this(new ObjectMapper(), serviceUrl, headers);
    }

    public JsonRpcHttpClient(ObjectMapper mapper, URL serviceUrl, Map<String, String> headers) {
        this(mapper, serviceUrl, headers, false, false);
    }

    public JsonRpcHttpClient(ObjectMapper mapper, URL serviceUrl, Map<String, String> headers, boolean gzipRequests, boolean acceptGzipResponses) {
        super(mapper);
        this.headers = new HashMap();
        this.connectionProxy = Proxy.NO_PROXY;
        this.connectionTimeoutMillis = '\uea60';
        this.readTimeoutMillis = 120000;
        this.sslContext = null;
        this.hostNameVerifier = null;
        this.contentType = "application/json-rpc";
        this.gzipRequests = false;
        this.serviceUrl = serviceUrl;
        this.headers.putAll(headers);
        this.gzipRequests = gzipRequests;
        if(acceptGzipResponses) {
            this.headers.put("Accept-Encoding", "gzip");
        }
        try {
            this.noAuthURL = (new URI(serviceUrl.getProtocol(), (String)null, serviceUrl.getHost(), serviceUrl.getPort(), serviceUrl.getPath(), serviceUrl.getQuery(), (String)null)).toURL();
        } catch (MalformedURLException var3) {
            throw new IllegalArgumentException(serviceUrl.toString(), var3);
        } catch (URISyntaxException var4) {
            throw new IllegalArgumentException(serviceUrl.toString(), var4);
        }

        this.authStr = serviceUrl.getUserInfo() == null?null:String.valueOf(Base64Coder.encode(serviceUrl.getUserInfo().getBytes(Charset.forName("ISO8859-1"))));

    }

    public JsonRpcHttpClient(URL serviceUrl) {
        this(new ObjectMapper(), serviceUrl, new HashMap());
    }

    @Override
    public void invoke(String methodName, Object argument) throws Throwable {
        this.invoke(methodName, argument, (Class)null, new HashMap());
    }
    @Override
    public Object invoke(String methodName, Object argument, Type returnType) throws Throwable {
        return this.invoke(methodName, argument, (Type)returnType, new HashMap());
    }
    @Override
    public Object invoke(String methodName, Object argument, Type returnType, Map<String, String> extraHeaders) throws Throwable {
        HttpURLConnection connection = this.prepareConnection(extraHeaders);

        Object var10;
        try {
            Throwable var8;
            Throwable var9;
            if(this.gzipRequests) {
                connection.setRequestProperty("Content-Encoding", "gzip");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                GZIPOutputStream gos = new GZIPOutputStream(baos);
                var8 = null;

                try {
                    super.invoke(methodName, argument, gos);
                } catch (Throwable var119) {
                    var9 = var119;
                    var8 = var119;
                    throw var119;
                } finally {
                    if(gos != null) {
                        if(var8 != null) {
                            try {
                                gos.close();
                            } catch (Throwable var114) {
                                var8.addSuppressed(var114);
                            }
                        } else {
                            gos.close();
                        }
                    }

                }

                connection.setFixedLengthStreamingMode(baos.size());
                connection.connect();
                connection.getOutputStream().write(baos.toByteArray());
            } else {
                connection.connect();
                OutputStream send = connection.getOutputStream();
                Throwable var131 = null;

                try {
                    super.invoke(methodName, argument, send);
                } catch (Throwable var120) {
                    var131 = var120;
                    throw var120;
                } finally {
                    if(send != null) {
                        if(var131 != null) {
                            try {
                                send.close();
                            } catch (Throwable var113) {
                                var131.addSuppressed(var113);
                            }
                        } else {
                            send.close();
                        }
                    }

                }
            }

            boolean useGzip = this.useGzip(connection);

            try {
                InputStream answer = this.getStream(connection.getInputStream(), useGzip);
                var8 = null;

                try {
                    Object var134;
                    try {
                        var134 = super.readResponse(returnType, answer);
                        return var134;
                    } catch (Throwable var118) {
                        var134 = var118;
                        var8 = var118;
                        throw var118;
                    }
                } finally {
                    if(answer != null) {
                        if(var8 != null) {
                            try {
                                answer.close();
                            } catch (Throwable var117) {
                                var8.addSuppressed(var117);
                            }
                        } else {
                            answer.close();
                        }
                    }

                }
            } catch (JsonMappingException var126) {
                throw var126;
            } catch (IOException var127) {
                if(connection.getErrorStream() == null) {
                    throw new HttpException("Caught error with no response body.", var127);
                }

                try {
                    InputStream answer = this.getStream(connection.getErrorStream(), useGzip);
                    var9 = null;

                    try {
                        var10 = super.readResponse(returnType, answer);
                    } catch (Throwable var116) {
                        var10 = var116;
                        var9 = var116;
                        throw var116;
                    } finally {
                        if(answer != null) {
                            if(var9 != null) {
                                try {
                                    answer.close();
                                } catch (Throwable var115) {
                                    var9.addSuppressed(var115);
                                }
                            } else {
                                answer.close();
                            }
                        }

                    }
                } catch (IOException var124) {
                    throw new HttpException(readErrorString(connection), var124);
                }
            }
        } finally {
            connection.disconnect();
        }

        return var10;
    }

    @Override
    public <T> T invoke(String s, Object o, Class<T> aClass) throws Throwable {
        return (T)this.invoke(s, o, (Type)Type.class.cast(aClass));
    }

    @Override
    public <T> T invoke(String s, Object o, Class<T> aClass, Map<String, String> map) throws Throwable {
        return (T)this.invoke(s, o, (Type)Type.class.cast(aClass), map);
    }

    private HttpURLConnection prepareConnection(Map<String, String> extraHeaders) throws IOException {

        HttpURLConnection connection = (HttpURLConnection)this.noAuthURL.openConnection(this.connectionProxy);
        connection.setConnectTimeout(this.connectionTimeoutMillis);
        connection.setReadTimeout(this.readTimeoutMillis);
        connection.setAllowUserInteraction(false);
        connection.setDefaultUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestMethod("POST");
        this.setupSsl(connection);
        this.addHeaders(extraHeaders, connection);
        connection.setRequestProperty("Authorization", "Basic " + this.authStr);
        return connection;
    }

    private boolean useGzip(HttpURLConnection connection) {
        String contentEncoding = connection.getHeaderField("Content-Encoding");
        return contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip");
    }

    private InputStream getStream(InputStream inputStream, boolean useGzip) throws IOException {
        return (InputStream)(useGzip?new GZIPInputStream(inputStream):inputStream);
    }

    private static String readErrorString(HttpURLConnection connection) {
        try {
            InputStream stream = connection.getErrorStream();
            Throwable var2 = null;

            String var35;
            try {
                StringBuilder buffer = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                Throwable var5 = null;

                try {
                    for(int ch = reader.read(); ch >= 0; ch = reader.read()) {
                        buffer.append((char)ch);
                    }
                } catch (Throwable var30) {
                    var5 = var30;
                    throw var30;
                } finally {
                    if(reader != null) {
                        if(var5 != null) {
                            try {
                                reader.close();
                            } catch (Throwable var29) {
                                var5.addSuppressed(var29);
                            }
                        } else {
                            reader.close();
                        }
                    }

                }

                var35 = buffer.toString();
            } catch (Throwable var32) {
                var2 = var32;
                throw var32;
            } finally {
                if(stream != null) {
                    if(var2 != null) {
                        try {
                            stream.close();
                        } catch (Throwable var28) {
                            var2.addSuppressed(var28);
                        }
                    } else {
                        stream.close();
                    }
                }

            }

            return var35;
        } catch (IOException var34) {
            return var34.getMessage();
        }
    }

    private void setupSsl(HttpURLConnection connection) {
        if(HttpsURLConnection.class.isInstance(connection)) {
            HttpsURLConnection https = (HttpsURLConnection)HttpsURLConnection.class.cast(connection);
            if(this.hostNameVerifier != null) {
                https.setHostnameVerifier(this.hostNameVerifier);
            }

            if(this.sslContext != null) {
                https.setSSLSocketFactory(this.sslContext.getSocketFactory());
            }
        }

    }

    private void addHeaders(Map<String, String> extraHeaders, HttpURLConnection connection) {
        connection.setRequestProperty("Content-Type", this.contentType);
        Iterator var3 = this.headers.entrySet().iterator();

        Entry entry;
        while(var3.hasNext()) {
            entry = (Entry)var3.next();
            connection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
        }

        var3 = extraHeaders.entrySet().iterator();

        while(var3.hasNext()) {
            entry = (Entry)var3.next();
            connection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
        }

    }

    public URL getServiceUrl() {
        return this.serviceUrl;
    }

    public void setServiceUrl(URL serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public Proxy getConnectionProxy() {
        return this.connectionProxy;
    }

    public void setConnectionProxy(Proxy connectionProxy) {
        this.connectionProxy = connectionProxy;
    }

    public int getConnectionTimeoutMillis() {
        return this.connectionTimeoutMillis;
    }

    public void setConnectionTimeoutMillis(int connectionTimeoutMillis) {
        this.connectionTimeoutMillis = connectionTimeoutMillis;
    }

    public int getReadTimeoutMillis() {
        return this.readTimeoutMillis;
    }

    public void setReadTimeoutMillis(int readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(this.headers);
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers.clear();
        this.headers.putAll(headers);
    }

    public void setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    public void setHostNameVerifier(HostnameVerifier hostNameVerifier) {
        this.hostNameVerifier = hostNameVerifier;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}