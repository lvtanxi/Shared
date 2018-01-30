package com.lvtanxi.shared;

import java.lang.reflect.Type;

public interface Parser {

  <T> T fromJson(String content, Type type) throws Exception;

  String toJson(Object body);

}
