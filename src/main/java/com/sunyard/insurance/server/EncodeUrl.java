package com.sunyard.insurance.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Properties;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class EncodeUrl
{
  private static final int END_OF_INPUT = -1;
  private static final int NON_BASE_64 = -1;
  private static final int NON_BASE_64_WHITESPACE = -2;
  private static final int NON_BASE_64_PADDING = -3;
  protected static final byte[] base64Chars = { 
    48, 121, 81, 67, 53, 70, 104, 84, 
    72, 73, 106, 74, 109, 95, 56, 75, 
    50, 77, 100, 78, 54, 65, 80, 99, 
    83, 85, 112, 86, 88, 68, 89, 120, 
    90, 97, 52, 66, 101, 102, 103, 105, 
    107, 45, 57, 108, 110, 71, 113, 82, 
    114, 69, 49, 115, 79, 116, 117, 118, 
    119, 122, 51, 98, 76, 55, 87, 111 };

  protected static final byte[] reverseBase64Chars = new byte[256];
  public static final String version = "1.2";
  private static final int BUFFER_SIZE = 1024;
  public static String[] hexChar;
  protected static String encodeKey;
  private static Hashtable<String, Properties> register;

  static
  {
    for (int i = 0; i < reverseBase64Chars.length; i++) {
      reverseBase64Chars[i] = -1;
    }

    for (byte i = 0; i < base64Chars.length; i = (byte)(i + 1)) {
      reverseBase64Chars[base64Chars[i]] = i;
    }
    reverseBase64Chars[32] = -2;
    reverseBase64Chars[10] = -2;
    reverseBase64Chars[13] = -2;
    reverseBase64Chars[9] = -2;
    reverseBase64Chars[12] = -2;
    reverseBase64Chars[61] = -3;

    hexChar = new String[] { "A", "b", "R", "h", "2", "H", "q", "4", 
      "c", "G", "9", "r", "0", "t", "U", "k" };

    encodeKey = "";

    register = new Hashtable();
  }

  public static String encode(String string)
  {
    return new String(encode(string.getBytes()));
  }

  public static String encode(String string, String enc)
    throws UnsupportedEncodingException
  {
    return new String(encode(string.getBytes(enc)), enc);
  }

  public static String encodeToString(byte[] bytes)
  {
    return encodeToString(bytes, false);
  }

  public static String encodeToString(byte[] bytes, boolean lineBreaks)
  {
	String retStr = "";
    try
    {
      retStr = new String(encode(bytes, lineBreaks), "ASCII");
    } catch (UnsupportedEncodingException iex) {
    }
    return retStr;
  }

  public static byte[] encode(byte[] bytes)
  {
    return encode(bytes, false);
  }

  public static byte[] encode(byte[] bytes, boolean lineBreaks)
  {
    ByteArrayInputStream in = new ByteArrayInputStream(bytes);

    int length = bytes.length;
    int mod;
    if ((mod = length % 3) != 0) {
      length += 3 - mod;
    }
    length = length * 4 / 3;
    ByteArrayOutputStream out = new ByteArrayOutputStream(length);
    try {
      encode(in, out, lineBreaks);
    }
    catch (IOException x)
    {
      throw new RuntimeException(x);
    }
    return out.toByteArray();
  }

  public static void encode(File fIn)
    throws IOException
  {
    encode(fIn, fIn, true);
  }

  public static void encode(File fIn, boolean lineBreaks)
    throws IOException
  {
    encode(fIn, fIn, lineBreaks);
  }

  public static void encode(File fIn, File fOut)
    throws IOException
  {
    encode(fIn, fOut, true);
  }

  public static void encode(File fIn, File fOut, boolean lineBreaks)
    throws IOException
  {
    File temp = null;
    InputStream in = null;
    OutputStream out = null;
    try {
      in = new BufferedInputStream(new FileInputStream(fIn));
      temp = File.createTempFile("Base64", null, null);
      out = new BufferedOutputStream(new FileOutputStream(temp));
      encode(in, out, lineBreaks);
      in.close();
      in = null;
      out.flush();
      out.close();
      out = null;
      move(temp, fOut, true);
    } finally {
      if (in != null) {
        in.close();
        in = null;
      }
      if (out != null) {
        out.flush();
        out.close();
        out = null;
      }
    }
  }

  public static void encode(InputStream in, OutputStream out)
    throws IOException
  {
    encode(in, out, true);
  }

  public static void encode(InputStream in, OutputStream out, boolean lineBreaks)
    throws IOException
  {
    int[] inBuffer = new int[3];
    int lineCount = 0;

    boolean done = false;
    while ((!done) && ((inBuffer[0] = in.read()) != -1))
    {
      inBuffer[1] = in.read();
      inBuffer[2] = in.read();

      out.write(base64Chars[(inBuffer[0] >> 2)]);
      if (inBuffer[1] != -1)
      {
        out.write(base64Chars[(inBuffer[0] << 4 & 0x30 | inBuffer[1] >> 4)]);
        if (inBuffer[2] != -1)
        {
          out.write(base64Chars[(inBuffer[1] << 2 & 0x3C | inBuffer[2] >> 6)]);

          out.write(base64Chars[(inBuffer[2] & 0x3F)]);
        }
        else {
          out.write(base64Chars[(inBuffer[1] << 2 & 0x3C)]);

          out.write(61);
          done = true;
        }
      }
      else {
        out.write(base64Chars[(inBuffer[0] << 4 & 0x30)]);

        out.write(61);
        out.write(61);
        done = true;
      }
      lineCount += 4;
      if ((lineBreaks) && (lineCount >= 76)) {
        out.write(10);
        lineCount = 0;
      }
    }
    if ((lineBreaks) && (lineCount >= 1)) {
      out.write(10);
      lineCount = 0;
    }
    out.flush();
  }

  public static String decode(String string)
  {
    return new String(decode(string.getBytes()));
  }

  public static String decode(String string, String enc)
    throws UnsupportedEncodingException
  {
    return new String(decode(string.getBytes(enc)), enc);
  }

  public static String decode(String string, String encIn, String encOut)
    throws UnsupportedEncodingException
  {
    return new String(decode(string.getBytes(encIn)), encOut);
  }

  public static String decodeToString(String string)
  {
    return new String(decode(string.getBytes()));
  }

  public static String decodeToString(String string, String enc)
    throws UnsupportedEncodingException
  {
    return new String(decode(string.getBytes(enc)), enc);
  }

  public static String decodeToString(String string, String encIn, String encOut)
    throws UnsupportedEncodingException
  {
    return new String(decode(string.getBytes(encIn)), encOut);
  }

  public static void decodeToStream(String string, OutputStream out)
    throws IOException
  {
    decode(new ByteArrayInputStream(string.getBytes()), out);
  }

  public static void decodeToStream(String string, String enc, OutputStream out)
    throws UnsupportedEncodingException, IOException
  {
    decode(new ByteArrayInputStream(string.getBytes(enc)), out);
  }

  public static byte[] decodeToBytes(String string)
  {
    return decode(string.getBytes());
  }

  public static byte[] decodeToBytes(String string, String enc)
    throws UnsupportedEncodingException
  {
    return decode(string.getBytes(enc));
  }

  public static String decodeToString(byte[] bytes)
  {
    return new String(decode(bytes));
  }

  public static String decodeToString(byte[] bytes, String enc)
    throws UnsupportedEncodingException
  {
    return new String(decode(bytes), enc);
  }

  public static byte[] decodeToBytes(byte[] bytes)
  {
    return decode(bytes);
  }

  public static byte[] decode(byte[] bytes)
  {
    ByteArrayInputStream in = new ByteArrayInputStream(bytes);

    int length = bytes.length;
    int mod;
    if ((mod = length % 4) != 0) {
      length += 4 - mod;
    }
    length = length * 3 / 4;
    ByteArrayOutputStream out = new ByteArrayOutputStream(length);
    try {
      decode(in, out, false);
    }
    catch (IOException x)
    {
      throw new RuntimeException(x);
    }
    return out.toByteArray();
  }

  public static void decode(byte[] bytes, OutputStream out)
    throws IOException
  {
    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    decode(in, out, false);
  }

  public static void decodeToStream(byte[] bytes, OutputStream out)
    throws IOException
  {
    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    decode(in, out, false);
  }

  public static void decode(File fIn)
    throws IOException
  {
    decode(fIn, fIn, true);
  }

  public static void decode(File fIn, boolean throwExceptions)
    throws IOException
  {
    decode(fIn, fIn, throwExceptions);
  }

  public static void decode(File fIn, File fOut)
    throws IOException
  {
    decode(fIn, fOut, true);
  }

  public static void decode(File fIn, File fOut, boolean throwExceptions)
    throws IOException
  {
    File temp = null;
    InputStream in = null;
    OutputStream out = null;
    try {
      in = new BufferedInputStream(new FileInputStream(fIn));
      temp = File.createTempFile("Base64", null, null);
      out = new BufferedOutputStream(new FileOutputStream(temp));
      decode(in, out, throwExceptions);
      in.close();
      in = null;
      out.flush();
      out.close();
      out = null;
      move(temp, fOut, true);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException ignore) {
          if (throwExceptions) throw ignore;
        }
        in = null;
      }
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ignore) {
          if (throwExceptions) throw ignore;
        }
        out = null;
      }
    }
  }

  private static final int readBase64(InputStream in, boolean throwExceptions) throws IOException {
    int numPadding = 0;
    int read;
    do {
      read = in.read();
      if (read == -1) return -1;
      read = reverseBase64Chars[(byte)read];
      if ((throwExceptions) && ((read == -1) || ((numPadding > 0) && (read > -1)))) {
        throw new IOException(
          MessageFormat.format(
          "Unexpected Base64 character: {0}", 
          new String[] { 
          "'" + (char)read + "' (0x" + Integer.toHexString(read) + ")" }));
      }

      if (read == -3)
        numPadding++;
    }
    while (
      read <= -1);
    return read;
  }

  public static byte[] decodeToBytes(InputStream in)
    throws IOException
  {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    decode(in, out, false);
    return out.toByteArray();
  }

  public static String decodeToString(InputStream in)
    throws IOException
  {
    return new String(decodeToBytes(in));
  }

  public static String decodeToString(InputStream in, String enc)
    throws IOException
  {
    return new String(decodeToBytes(in), enc);
  }

  public static void decode(InputStream in, OutputStream out)
    throws IOException
  {
    decode(in, out, true);
  }

  public static void decode(InputStream in, OutputStream out, boolean throwExceptions)
    throws IOException
  {
    int[] inBuffer = new int[4];

    boolean done = false;
    while ((!done) && ((inBuffer[0] = readBase64(in, throwExceptions)) != -1) && 
      ((inBuffer[1] = readBase64(in, throwExceptions)) != -1))
    {
      inBuffer[2] = readBase64(in, throwExceptions);
      inBuffer[3] = readBase64(in, throwExceptions);

      out.write(inBuffer[0] << 2 | inBuffer[1] >> 4);
      if (inBuffer[2] != -1)
      {
        out.write(inBuffer[1] << 4 | inBuffer[2] >> 2);
        if (inBuffer[3] != -1)
        {
          out.write(inBuffer[2] << 6 | inBuffer[3]);
        }
        else done = true; 
      }
      else
      {
        done = true;
      }
    }
    out.flush();
  }

  public static boolean isBase64(byte[] bytes)
  {
    try
    {
      return isBase64(new ByteArrayInputStream(bytes));
    }
    catch (IOException x)
    {
    }
    return false;
  }

  public static boolean isBase64(String string)
  {
    return isBase64(string.getBytes());
  }

  public static boolean isBase64(String string, String enc)
    throws UnsupportedEncodingException
  {
    return isBase64(string.getBytes(enc));
  }

  public static boolean isBase64(File fIn)
    throws IOException
  {
    return isBase64(new BufferedInputStream(new FileInputStream(fIn)));
  }

  public static boolean isBase64(InputStream in)
    throws IOException
  {
    long numBase64Chars = 0L;
    int numPadding = 0;
    int read;
    while ((read = in.read()) != -1) {
      if (read == -1)
        return false;
      if (read == -2)
        continue;
      if (read == -3) {
        numPadding++;
        numBase64Chars += 1L; } else {
        if (numPadding > 0) {
          return false;
        }
        numBase64Chars += 1L;
      }
    }
    if (numBase64Chars == 0L) return false;
    return numBase64Chars % 4L == 0L;
  }

  public static void move(File from, File to, boolean overwrite) throws IOException
  {
    if (to.exists()) {
      if (overwrite) {
        if (!to.delete()) {
          throw new IOException(
            MessageFormat.format(
            "{0} could not be deleted.", 
            new String[] { 
            to.toString() }));
        }

      }
      else
      {
        throw new IOException(
          MessageFormat.format(
          "{0} already exists.", 
          new String[] { 
          to.toString() }));
      }

    }

    if (from.renameTo(to)) return;

    InputStream in = null;
    OutputStream out = null;
    try {
      in = new FileInputStream(from);
      out = new FileOutputStream(to);
      copy(in, out);
      in.close();
      in = null;
      out.flush();
      out.close();
      out = null;
      if (!from.delete()) {
        throw new IOException(
          MessageFormat.format(
          "{0} copied to {1} but original could not be deleted.", 
          new String[] { 
          from.toString(), 
          to.toString() }));
      }

    }
    finally
    {
      if (in != null) {
        in.close();
        in = null;
      }
      if (out != null) {
        out.flush();
        out.close();
        out = null;
      }
    }
    if (in != null) {
      in.close();
      in = null;
    }
    if (out != null) {
      out.flush();
      out.close();
      out = null;
    }
  }

  private static void copy(InputStream in, OutputStream out)
    throws IOException
  {
    byte[] buffer = new byte[1024];
    int read;
    while ((read = in.read(buffer)) != -1)
    {
      out.write(buffer, 0, read);
    }
  }

  public static String encryptHmacMd5Str(String key, String inStr) throws NoSuchAlgorithmException, InvalidKeyException {
    if ((inStr == null) || ("".equals(inStr))) {
      throw new IllegalArgumentException("Parameter[inStr] can't be null.");
    }
    if ((key == null) || ("".equals(key))) {
      throw new IllegalArgumentException("Parameter[key] can't be null.");
    }
    char[] charArray = inStr.toCharArray();
    byte[] byteArray = new byte[charArray.length];
    for (int i = 0; i < charArray.length; i++) {
      byteArray[i] = (byte)charArray[i];
    }

    SecretKeySpec sk = new SecretKeySpec(key.getBytes(), "HmacMD5");
    Mac mac = Mac.getInstance("HmacMD5");
    mac.init(sk);

    byte[] md5Bytes = mac.doFinal(byteArray);
    return toHexString(md5Bytes);
  }

  public static String toHexString(byte[] b) {
    StringBuilder sb = new StringBuilder(b.length * 2);
    for (int i = 0; i < b.length; i++) {
      sb.append(hexChar[((b[i] & 0xF0) >>> 4)]);
      sb.append(hexChar[(b[i] & 0xF)]);
    }
    return sb.toString();
  }

  public static String getConfigValue(String fileName, String key)
    throws IOException
  {
    String fileSeparator = System.getProperty("file.separator");
    Properties props = new Properties();
    BufferedReader bf = null;
    if ((GlobalSafeVar.CONFIG_PATH != null) && (!GlobalSafeVar.CONFIG_PATH.trim().equals(""))) {
      props = getProperties(GlobalSafeVar.CONFIG_PATH + fileSeparator + fileName);
    } else {
      bf = getFileInByClsPth(fileName);
      props.load(bf);
    }
    return (String)props.get(key);
  }

  public static Properties getConfig(String fileName) throws IOException {
    String fileSeparator = System.getProperty("file.separator");
    Properties props = new Properties();
    BufferedReader bf = null;
    if ((GlobalSafeVar.CONFIG_PATH != null) && (!GlobalSafeVar.CONFIG_PATH.trim().equals(""))) {
      props = getProperties(GlobalSafeVar.CONFIG_PATH + fileSeparator + fileName);
    } else {
      bf = getFileInByClsPth(fileName);
      props.load(bf);
    }
    return props;
  }

  public static Properties getProperties(String fileName)
    throws IOException
  {
    InputStream is = null;
    Properties p = null;
    p = (Properties)register.get(fileName);

    if (p == null) {
      try {
        is = new FileInputStream(fileName);
      } catch (Exception e) {
        if (fileName.startsWith("/"))
        {
          is = EncodeUrl.class.getClassLoader().getResourceAsStream(fileName);
        }if ((fileName.indexOf("/") < 0) || (fileName.indexOf("/") < 0))
          is = EncodeUrl.class.getClassLoader().getResourceAsStream(fileName);
        else {
          is = EncodeUrl.class.getClassLoader().getResourceAsStream("/" + fileName);
        }
      }
      p = new Properties();
      BufferedReader bf = new BufferedReader(new InputStreamReader(is, "utf-8"));
      p.load(bf);
      register.put(fileName, p);
      is.close();
    }
    return p;
  }

  public static BufferedReader getFileInByClsPth(String path) throws IOException {
    InputStream in = EncodeUrl.class.getClassLoader().getResourceAsStream(path);
    BufferedReader bf = new BufferedReader(new InputStreamReader(in, "utf-8"));
    return bf;
  }

  public static String encodeParam(String param, String encode) throws Exception {
    if ("".equals(encodeKey)) {
      encodeKey = getConfigValue("SafeConfig.properties", "encodeKey");
    }
    if (encodeKey.length() < 16) {
      throw new Exception("SafeConfig.properties配置私有密钥小于16位，请重新配置！");
    }
    String token = "&token=";
    String paramMd5 = encryptHmacMd5Str(encodeKey, param);
    token = token + paramMd5;
    String retParam = param + token;
    return encode(retParam, encode);
  }

  public static void main(String[] args) {
    try {
      encodeKey = "8fh4d0dkbv74hsv7";
      String desStr = encodeParam("data=20160627&file_name=C://TRM_CONFIG/TRMConfig.properties", "utf-8");
      String outSesStr = "http://127.0.0.1:7001/SunTRM/servlet/GetImage?" + desStr;
      System.out.println(outSesStr);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}