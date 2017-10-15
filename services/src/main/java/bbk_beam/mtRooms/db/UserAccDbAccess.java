package bbk_beam.mtRooms.db;

import bbk_beam.mtRooms.db.database.IUserAccDb;
import bbk_beam.mtRooms.db.exception.*;
import bbk_beam.mtRooms.db.session.ICurrentSessions;
import bbk_beam.mtRooms.db.session.SessionType;
import eadjlib.datastructure.ObjectTable;
import eadjlib.logger.Logger;

import java.sql.SQLException;
import java.util.Date;

public class UserAccDbAccess implements IUserAccDbAccess {
    private final Logger log = Logger.getLoggerInstance(UserAccDbAccess.class.getName());
    private ICurrentSessions currentSessions;
    private IUserAccDb db;

    /**
     * Constructor
     *
     * @param session_tracker Current session tracker instance
     * @param db              Current Database instance
     * @throws SQLException     when connection to the database fails
     * @throws DbBuildException when database is corrupted or incomplete
     */
    UserAccDbAccess(ICurrentSessions session_tracker, IUserAccDb db) throws SQLException, DbBuildException {
        this.currentSessions = session_tracker;
        this.db = db;

        if (!db.isConnected()) {
            if (!db.connect()) {
                log.log_Fatal("Could not connect to database.");
                throw new SQLException("Could not connect to database.");
            } else {
                try {
                    if (!db.checkUserAccDB()) {
                        log.log_Fatal("User tables in database either corrupted or incomplete.");
                        throw new DbBuildException("Database corruption detected.");
                    }
                } catch (DbEmptyException e) {
                    if (!db.setupUserAccDB()) {
                        log.log_Fatal("Could not build user new database structure.");
                        throw new DbBuildException("Could not build new database structure.");
                    }
                }
            }
        }
    }

    @Override
    public boolean checkValidity(String session_id) {
        try {
            return this.currentSessions.isValid(session_id);
        } catch (SessionInvalidException e) {
            log.log_Error("Session [", session_id, "] does not exist in the tracker.");
            return false;
        }
    }

    @Override
    public void openSession(String session_id, Date expiry, SessionType session_type) throws SessionException {
        this.currentSessions.addSession(session_id, expiry, session_type);
    }

    @Override
    public void closeSession(String session_id) throws SessionInvalidException {
        this.currentSessions.removeSession(session_id);
    }

    @Override
    public boolean pushToDB(String query) throws DbQueryException {
        return this.db.push(query);
    }

    @Override
    public ObjectTable pullFromDB(String query) throws DbQueryException {
        return this.db.pull(query);
    }

}
