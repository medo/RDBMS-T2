package team2.engine;

import java.util.Hashtable;
import java.util.Iterator;

import team2.commands.CreateTableCommand;
import team2.commands.DeleteCommand;
import team2.commands.SelectCommand;
import team2.exceptions.DBAppException;
import team2.exceptions.DBEngineException;
import team2.util.CSVReader;
import team2.util.btrees.BTreeFactory;


public class Engine {
	
	BTreeFactory bTreeFactory;
	CSVReader reader;
	
	public void init(){
		// TODO
	}
	
	public void createTable(String strTableName,
							Hashtable<String,String> htblColNameType,
							Hashtable<String,String>htblColNameRefs,
							String strKeyColName) 
									throws DBEngineException {
	CreateTableCommand newTable = new CreateTableCommand(strTableName, htblColNameType, htblColNameRefs, strKeyColName, this.reader);
	newTable.execute(); 
	
	}
	
	public void createIndex(String strTableName, String strColName) throws DBAppException {
		// TODO
	}
	
	public void insertIntoTable(String strTableName,
								Hashtable<String,String> htblColNameValue)
										throws DBAppException {
		// TODO
	}
	
	public void deleteFromTable(String strTableName,
								Hashtable<String,String> htblColNameValue,
								String strOperator)
										throws DBEngineException {
		DeleteCommand deleteRow = new DeleteCommand(strTableName, htblColNameValue, strOperator, reader);
		deleteRow.execute(); 
	
	}
	
	public Iterator< Hashtable<String, String >> selectFromTable(String strTable,
									Hashtable<String,String> htblColNameValue,
									String strOperator)
											throws DBEngineException {
		SelectCommand selectCommand = new SelectCommand(this.bTreeFactory, this.reader, strTable, htblColNameValue, strOperator);
		selectCommand.execute();
		return selectCommand.getResults().iterator();
				
	}
	
	public void saveAll() throws DBEngineException {
		// TODO
	}
}
