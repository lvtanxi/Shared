package com.lvtanxi.shared;

public class DefaultSharedFacade implements SharedFacade {

  private final Storage storage;
  private final Converter converter;
  private final Encryption encryption;
  private final Serializer serializer;
  private final LogInterceptor logInterceptor;

  public DefaultSharedFacade(SharedBuilder builder) {
    encryption = builder.getEncryption();
    storage = builder.getStorage();
    converter = builder.getConverter();
    serializer = builder.getSerializer();
    logInterceptor = builder.getLogInterceptor();

    logInterceptor.onLog("Shared.init -> Encryption : " + encryption.getClass().getSimpleName());
  }

  @Override public <T> boolean put(String key, T value) {
    // Validate
    SharedUtils.checkNull("Key", key);
    log("Shared.put -> key: " + key + ", value: " + value);

    // If the value is null, delete it
    if (value == null) {
      log("Shared.put -> Value is null. Any existing value will be deleted with the given key");
      return delete(key);
    }

    // 1. Convert to text
    String plainText = converter.toString(value);
    log("Shared.put -> Converted to " + plainText);
    if (plainText == null) {
      log("Shared.put -> Converter failed");
      return false;
    }

    // 2. Encrypt the text
    String cipherText = null;
    try {
      cipherText = encryption.encrypt(key, plainText);
      log("Shared.put -> Encrypted to  " + cipherText);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (cipherText == null) {
      log("Shared.put -> Encryption failed");
      return false;
    }

    // 3. Serialize the given object along with the cipher text
    String serializedText = serializer.serialize(cipherText, value);
    log("Shared.put -> Serialized to" + serializedText);
    if (serializedText == null) {
      log("Shared.put -> Serialization failed");
      return false;
    }

    // 4. Save to the storage
    if (storage.put(key, serializedText)) {
      log("Shared.put -> Stored successfully");
      return true;
    } else {
      log("Shared.put -> Store operation failed");
      return false;
    }
  }

  @Override public <T> T get(String key) {
    log("Shared.get -> key: " + key);
    if (key == null) {
      log("Shared.get -> null key, returning null value ");
      return null;
    }

    // 1. Get serialized text from the storage
    String serializedText = storage.get(key);
    log("Shared.get -> Fetched from storage : " + serializedText);
    if (serializedText == null) {
      log("Shared.get -> Fetching from storage failed");
      return null;
    }

    // 2. Deserialize
    DataInfo dataInfo = serializer.deserialize(serializedText);
    log("Shared.get -> Deserialized");
    if (dataInfo == null) {
      log("Shared.get -> Deserialization failed");
      return null;
    }

    // 3. Decrypt
    String plainText = null;
    try {
      plainText = encryption.decrypt(key, dataInfo.cipherText);
      log("Shared.get -> Decrypted to : " + plainText);
    } catch (Exception e) {
      log("Shared.get -> Decrypt failed: " + e.getMessage());
    }
    if (plainText == null) {
      log("Shared.get -> Decrypt failed");
      return null;
    }

    // 4. Convert the text to original data along with original type
    T result = null;
    try {
      result = converter.fromString(plainText, dataInfo);
      log("Shared.get -> Converted to : " + result);
    } catch (Exception e) {
      log("Shared.get -> Converter failed");
    }

    return result;
  }

  @Override public <T> T get(String key, T defaultValue) {
    T t = get(key);
    if (t == null) return defaultValue;
    return t;
  }

  @Override public long count() {
    return storage.count();
  }

  @Override public boolean deleteAll() {
    return storage.deleteAll();
  }

  @Override public boolean delete(String key) {
    return storage.delete(key);
  }

  @Override public boolean contains(String key) {
    return storage.contains(key);
  }

  @Override public boolean isBuilt() {
    return true;
  }

  @Override public void destroy() {
  }

  private void log(String message) {
    logInterceptor.onLog(message);
  }
}
