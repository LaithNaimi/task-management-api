package com.laith.taskmanagement.exception;

import java.net.URI;

public final class ProblemTypes {
    private ProblemTypes() {}

    public static final URI VALIDATION = URI.create("urn:problem:validation");
    public static final URI MALFORMED_JSON = URI.create("urn:problem:malformed-json");
    public static final URI INVALID_QUERY_PARAM = URI.create("urn:problem:invalid-query-param");

    public static final URI TASK_NOT_FOUND = URI.create("urn:problem:task-not-found");

    public static final URI CATEGORY_NOT_FOUND = URI.create("urn:problem:category-not-found");
    public static final URI CATEGORY_ALREADY_EXISTS = URI.create("urn:problem:category-already-exists");
    public static final URI CATEGORY_IN_USE = URI.create("urn:problem:category-in-use");

    public static final URI BAD_REQUEST = URI.create("urn:problem:bad-request");
    public static final URI INTERNAL_ERROR = URI.create("urn:problem:internal-error");

    public static final URI USER_NOT_FOUND = URI.create("urn:problem:user-not-found");


    public static final URI METHOD_NOT_ALLOWED = URI.create("urn:problem:method-not-allowed");
    public static final URI CONFLICT = URI.create("urn:problem:conflict");

    public static final URI UNAUTHORIZED = URI.create("urn:problem:unauthorized");
    public static final URI FORBIDDEN = URI.create("urn:problem:forbidden");

    public static final URI EMAIL_ALREADY_EXISTS = URI.create("urn:problem:email-already-exists");
    public static final URI INVALID_CREDENTIALS = URI.create("urn:problem:invalid-credentials");

}
