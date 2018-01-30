package com.lvtanxi.shared;

import android.content.Context;


public class SharedBuilder {

  /**
   * NEVER ever change STORAGE_TAG_DO_NOT_CHANGE and TAG_INFO.
   * It will break backward compatibility in terms of keeping previous data
   */
  private static final String STORAGE_TAG_DO_NOT_CHANGE = "Shared";

  private Context context;
  private Storage cryptoStorage;
  private Converter converter;
  private Parser parser;
  private Encryption encryption;
  private Serializer serializer;
  private LogInterceptor logInterceptor;

  public SharedBuilder(Context context) {
    SharedUtils.checkNull("Context", context);

    this.context = context.getApplicationContext();
  }

  public SharedBuilder setStorage(Storage storage) {
    this.cryptoStorage = storage;
    return this;
  }

  public SharedBuilder setParser(Parser parser) {
    this.parser = parser;
    return this;
  }

  public SharedBuilder setSerializer(Serializer serializer) {
    this.serializer = serializer;
    return this;
  }

  public SharedBuilder setLogInterceptor(LogInterceptor logInterceptor) {
    this.logInterceptor = logInterceptor;
    return this;
  }

  public SharedBuilder setConverter(Converter converter) {
    this.converter = converter;
    return this;
  }

  public SharedBuilder setEncryption(Encryption encryption) {
    this.encryption = encryption;
    return this;
  }

  LogInterceptor getLogInterceptor() {
    if (logInterceptor == null) {
      logInterceptor = new LogInterceptor() {
        @Override public void onLog(String message) {
          //empty implementation
        }
      };
    }
    return logInterceptor;
  }

  Storage getStorage() {
    if (cryptoStorage == null) {
      cryptoStorage = new SharedPreferencesStorage(context, STORAGE_TAG_DO_NOT_CHANGE);
    }
    return cryptoStorage;
  }

  Converter getConverter() {
    if (converter == null) {
      converter = new SharedConverter(getParser());
    }
    return converter;
  }

  Parser getParser() {
    if (parser == null) {
      parser = new FastJsonParser();
    }
    return parser;
  }

  Encryption getEncryption() {
    if (encryption == null) {
      encryption = new NoEncryption();
    }
    return encryption;
  }

  Serializer getSerializer() {
    if (serializer == null) {
      serializer = new SharedSerializer(getLogInterceptor());
    }
    return serializer;
  }

  public void build() {
    Shared.build(this);
  }
}
