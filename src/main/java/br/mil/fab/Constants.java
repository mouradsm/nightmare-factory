package br.mil.fab;

public final class Constants {

    private Constants() {}

    /** API Route **/
    public static final String API_GET = "/select/:table_name";
    public static final String API_CREATE = "/insert/:table_name";
    public static final String API_UPDATE = "/update/:table_name/:id";
    public static final String API_DELETE = "/delete/:table_name/:id";
    public static final String API_DELETE_ALL = "/delete/:table_name";

}
