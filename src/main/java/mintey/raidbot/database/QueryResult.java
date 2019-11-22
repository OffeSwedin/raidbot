package mintey.raidbot.database;

import java.sql.ResultSet;
import java.sql.Statement;
public class QueryResult {
    private final Statement stmt;
    private final ResultSet results;
    public QueryResult(Statement stmt, ResultSet resultSet) {
        this.stmt = stmt;
        this.results = resultSet;
    }
    public Statement getStmt() {
        return stmt;
    }
    public ResultSet getResults() {
        return results;
    }
}
