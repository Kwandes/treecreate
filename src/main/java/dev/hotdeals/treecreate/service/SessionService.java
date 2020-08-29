package dev.hotdeals.treecreate.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class SessionService
{
    public static String getSessionName(HttpServletRequest request)
    {
        return request.getSession().getId() + "|" + request.getSession().getAttribute("username") +
                ":" + request.getSession().getAttribute("userId");
    }
}
