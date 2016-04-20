package br.com.anteros.android.synchronism.communication;

import java.util.List;


/**
 *
 * @author Edson Martins
 */
public class TableExport {

    public String tableName;
    public String tableMobileName;
    @SuppressWarnings("rawtypes")
	public List requests;

    public TableExport(String tableName, String tableMobileName) {
        this.tableName = tableName;
        this.tableMobileName = tableMobileName;
    }


}
