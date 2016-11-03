package com.miniprofiler;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class IpAddressProviderTest {
    private IpAddressProvider addressProvider = new IpAddressProvider();

    @Test
    public void testHasNoAddress() {
        ServletRequest request = mock(ServletRequest.class);
        assertEquals("", addressProvider.getUser(request));
    }

    @Test
    public void testRemoteAddressOnly() {
        ServletRequest request = mock(ServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("1.2.3.4");
        assertEquals("1.2.3.4", addressProvider.getUser(request));
    }

    @Test
    public void testXffHeaderOnly() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(eq("X-Forwarded-For"))).thenReturn("4.3.2.1");
        assertEquals(" - 4.3.2.1", addressProvider.getUser(request));
    }

    @Test
    public void testRemoteAddressAndXffHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("1.2.3.4");
        when(request.getHeader(eq("X-Forwarded-For"))).thenReturn("4.3.2.1");
        assertEquals("1.2.3.4 - 4.3.2.1", addressProvider.getUser(request));
    }
}