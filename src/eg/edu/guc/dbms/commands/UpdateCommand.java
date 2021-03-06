package eg.edu.guc.dbms.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import eg.edu.guc.dbms.components.BufferManager;
import eg.edu.guc.dbms.components.LogManagerImpl;
import eg.edu.guc.dbms.exceptions.DBEngineException;
import eg.edu.guc.dbms.helpers.Page;
import eg.edu.guc.dbms.interfaces.Command;
import eg.edu.guc.dbms.interfaces.LogManager;
import eg.edu.guc.dbms.utils.CSVReader;
import eg.edu.guc.dbms.utils.Properties;
import eg.edu.guc.dbms.utils.Utils;
import eg.edu.guc.dbms.utils.btrees.BTreeAdopter;
import eg.edu.guc.dbms.utils.btrees.BTreeFactory;

public class UpdateCommand implements Command {
	CSVReader reader;
	String tableName;
	HashMap<String, String> hMapColNameValue;
	HashMap<String, String> colValue;
	String strOperator;
	Properties properties;
	BTreeFactory btfactory;
	SelectCommand select;
	BufferManager bufferManager;
	int pageId;
	LogManager logManager;
	long transactionNumber = 1;

	public UpdateCommand(BTreeFactory btfactory, CSVReader reader,
			Properties properties, String tableName,
			HashMap<String, String> hMapColNameValue, String strOperator,
			HashMap<String, String> colValue, BufferManager bufferManager,
			long transactionNumber) {
		this.btfactory = btfactory;
		this.reader = reader;
		this.tableName = tableName;
		this.hMapColNameValue = hMapColNameValue;
		this.strOperator = strOperator;
		this.colValue = colValue;
		this.bufferManager = bufferManager;
		this.properties = properties;
		this.transactionNumber = transactionNumber;
		this.logManager = LogManagerImpl.getInstance();
		select = new SelectCommand(this.btfactory, this.reader,
				this.properties, this.bufferManager, this.tableName,
				this.colValue, this.strOperator, transactionNumber);

	}
	
	public UpdateCommand(BTreeFactory btfactory, CSVReader reader,
			Properties properties, String tableName,
			HashMap<String, String> hMapColNameValue, String strOperator,
			HashMap<String, String> colValue, BufferManager bufferManager,
			long transactionNumber, LogManager logManager) {
		this(btfactory, reader, properties, tableName, hMapColNameValue,
				strOperator, colValue, bufferManager, transactionNumber);
		this.logManager = logManager;
		select = new SelectCommand(this.btfactory, this.reader,
				this.properties, this.bufferManager, this.tableName,
				this.colValue, this.strOperator, transactionNumber);

	}

	@Override
	public void execute() throws DBEngineException, IOException {
		this.validate();
		select.execute();
		this.updateTree();
		this.updateTable();

	}

	public void updateTable() throws DBEngineException {
		ArrayList<String> pointers = select.getResultPointers();
		for (int i = 0; i < pointers.size(); i++) {
			String[] x = ((String) pointers.get(i)).split(" ");
			int pageNumber = Integer.parseInt(x[1]);
			int rowNumber = Integer.parseInt(x[2]);
			Page page = bufferManager.read(transactionNumber, tableName,
					pageNumber, true);
			page.add(updateValues(page,pageNumber, rowNumber));
			bufferManager.write(transactionNumber, tableName, pageNumber, page);
		}
	}

	public void updateTree() throws DBEngineException, IOException {
		ArrayList<String> indexedColumns = properties
				.getIndexedColumns(tableName);
		ArrayList<String> pointers = select.getResultPointers();
		List<HashMap<String, String>> results = select.getResult();
		for (int i = 0; i < indexedColumns.size(); i++) {
			BTreeAdopter adoptor = btfactory.getBtree(tableName,
					indexedColumns.get(i));
			for (int j = 0; j < pointers.size(); j++) {

				if (hMapColNameValue.get(indexedColumns.get(i)) != null) {
					adoptor.delete(results.get(j).get(indexedColumns.get(i)),
							pointers.get(j));
					adoptor.insert(indexedColumns.get(i),
							hMapColNameValue.get(indexedColumns.get(i)));
				}
			}
		}
	}

	public HashMap<String, String> updateValues(Page page,int pageNumber, int rowNumber) {
		HashMap<String, String> input = page.get(rowNumber);
		Set<String> columnName = hMapColNameValue.keySet();
		String[] columnNames = Utils.setToArray(columnName);
		for (int i = 0; i < columnNames.length; i++) {
			input.put(columnNames[i], hMapColNameValue.get(columnNames[i]));
		}
		return input;
	}

	public void validate() throws DBEngineException {
		if (properties.getData().get(tableName) == null) {
			throw new DBEngineException("Table name is wrong or doesn't exist.");
		}
		Set<String> columnName = hMapColNameValue.keySet();
		String[] columnNames = Utils.setToArray(columnName);
		for (int i = 0; i < columnNames.length; i++) {
			if (properties.getData().get(tableName).get(columnNames[i]) == null) {
				throw new DBEngineException(
						"Column name is wrong or doesn't exist.");
			}
		}
	}

	@Override
	public List<HashMap<String, String>> getResult() {
		// TODO Auto-generated method stub
		return null;
	}

}
