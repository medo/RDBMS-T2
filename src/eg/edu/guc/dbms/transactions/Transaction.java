package eg.edu.guc.dbms.transactions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import eg.edu.guc.dbms.components.BufferManager;
import eg.edu.guc.dbms.exceptions.DBEngineException;
import eg.edu.guc.dbms.interfaces.Command;
import eg.edu.guc.dbms.interfaces.LogManager;
import eg.edu.guc.dbms.interfaces.TransactionCallbackInterface;

public class Transaction extends Thread {

	private BufferManager bufferManager;
	private LogManager logManager;
	private List<Command> steps;
	private TransactionCallbackInterface callback;
	private long id;
	
	public Transaction(BufferManager bufManager, LogManager logManager,
            List<Command> vSteps) {
		this.bufferManager	= bufManager;
		this.logManager		= logManager;
		this.steps			= vSteps;
		this.id 			= (long) Math.random() * 1000000;
	}
	
	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return id;
	}
	
	
	public void execute(TransactionCallbackInterface callback) {
		this.callback = callback;
		start();
	}

	@Override
	public void run() {
		try {
			logManager.recordStart("" + getId());
			List< HashMap<String, String> > results = null;
			int i = 0;
			for (Command step : steps) {
				try {
					step.execute();
					results = step.getResult();
					if (i > 0)
						System.out.println(step.getClass().getName() + steps.get(i - 1).getResult());
					i++;
					System.out.println(step.getResult());
				} catch (IOException e) {
					e.printStackTrace();
					break;
				} catch (DBEngineException e) {
					e.printStackTrace();
					break;
				}
			}
			logManager.recordCommit("" + getId());
			logManager.flushLog();
			callback.onPostExecute(results);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
}
