package db.jdbc.connection;

/**
 * abstract로 객체 생성 막기
 */
public abstract class ConnectionConst {
    public static final String URL = "jdbc:h2:tcp://localhost/~/test"; //규약
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "";
}
