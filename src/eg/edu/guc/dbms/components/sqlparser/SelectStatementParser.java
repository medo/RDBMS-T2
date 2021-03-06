package eg.edu.guc.dbms.components.sqlparser;

import eg.edu.guc.dbms.sql.PhysicalPlanTree;
import eg.edu.guc.dbms.sql.Product;
import eg.edu.guc.dbms.sql.Project;
import eg.edu.guc.dbms.sql.Scan;
import eg.edu.guc.dbms.sql.Select;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class SelectStatementParser {
	public static PhysicalPlanTree parse(TSelectSqlStatement statement) {
//		System.out.println("Parse -> " + statement.toString());
		Project result = new Project();
		TResultColumnList temp = statement.getResultColumnList();
		for (int i = 0; i < temp.size(); i++) {
			TResultColumn resultColumn = temp.getResultColumn(i);
			result.addProjectionColumn(resultColumn.getExpr().toString());
//			System.out.printf("\tColumn: %s, Alias: %s\n",
//					resultColumn.getExpr().toString(), 
//					(resultColumn.getAliasClause() == null) ? "" : resultColumn.getAliasClause().toString()
//				);
		}
		
		PhysicalPlanTree next = null, current = result;
		
		if (statement.getWhereClause() != null) {
			String whereClause = statement.getWhereClause().getCondition().toString();
//			System.out.printf("\nWhere clause: \n\t%s\n", whereClause);
			next = new Select();
			next.setWhereClause(whereClause);
			current.addChild(next);
			current = next;
		}
		
		for (int i = 0; i < statement.joins.size(); i++) {
			TJoin join = statement.joins.getJoin(i);
//			System.out.printf("\nTable: \n\t%s, Alias: %s\n", join.getTable().toString(),
//					(join.getTable().getAliasClause() != null) ? join
//							.getTable().getAliasClause().toString() : "");
			
			if(i > 0) { // Product Tree
				next = new Product();
				next.addChild(current.popLastChild());
				next.addChild(new Scan(join.getTable().toString()));
				current.addChild(next);
				current = next;
			} else { // Single table
				next = new Scan(join.getTable().toString());
				current.addChild(next);
			}
		}
		return result;
	}
}
