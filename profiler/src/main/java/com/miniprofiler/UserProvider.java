package com.miniprofiler;

import javax.servlet.ServletRequest;

public interface UserProvider {
    String getUser(ServletRequest request);
}
