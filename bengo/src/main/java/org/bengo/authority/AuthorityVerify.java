package org.bengo.authority;

import javax.servlet.http.HttpServletRequest;


public interface AuthorityVerify {

    Boolean authorityVerify(HttpServletRequest request, String[] permissions);
}
