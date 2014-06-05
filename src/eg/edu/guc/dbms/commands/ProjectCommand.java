package eg.edu.guc.dbms.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import eg.edu.guc.dbms.exceptions.DBEngineException;
import eg.edu.guc.dbms.interfaces.Command;

public class ProjectCommand implements Command {

	private List<Hashtable<String, String>> result;
	private List<Hashtable<String, String>> source;
	private List<String> projectionColumn;

	public ProjectCommand(List<Hashtable<String, String>> source,
			List<String> projectionColumn) {
		result = new ArrayList<Hashtable<String, String>>();
		this.source = source;
		this.projectionColumn = projectionColumn;
	}

	@Override
	public void execute() throws DBEngineException, IOException {
		for (int i = 0; i < source.size(); i++) {
			Set<String> s = source.get(i).keySet();
			Hashtable<String, String> record = new Hashtable<String, String>();
			for (String key : s) {
				for (int k = 0; k < projectionColumn.size(); k++) {
					if (key.equals(projectionColumn.get(k))) {
						record.put(key, source.get(i).get(key));
					}
				}
			}
			result.add(record);
		}
	}

	@Override
	public List<Hashtable<String, String>> getResult() {
		return result;
	}

}