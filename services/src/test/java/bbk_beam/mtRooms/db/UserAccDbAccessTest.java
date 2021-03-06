package bbk_beam.mtRooms.db;

import bbk_beam.mtRooms.db.database.Database;
import bbk_beam.mtRooms.db.exception.*;
import bbk_beam.mtRooms.db.session.SessionTracker;
import bbk_beam.mtRooms.db.session.SessionType;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;

import static org.mockito.Mockito.*;

public class UserAccDbAccessTest {
    private Database mocked_Database;
    private SessionTracker mocked_SessionTracker;

    @org.junit.Before
    public void setUp() throws Exception {
        mocked_Database = mock(Database.class);
        mocked_SessionTracker = mock(SessionTracker.class);
    }

    @org.junit.After
    public void tearDown() throws Exception {
        mocked_Database = null;
        mocked_SessionTracker = null;
    }

    @Test(expected = SQLException.class)
    public void constructor_failed_db_connect() throws Exception {
        when(mocked_Database.connect()).thenReturn(false);
        UserAccDbAccess userAccDbAccess = new UserAccDbAccess(mocked_SessionTracker, mocked_Database);
    }

    @Test(expected = DbBuildException.class)
    public void constructor_failed_db_check() throws Exception {
        when(mocked_Database.connect()).thenReturn(true);
        when(mocked_Database.checkUserAccDB()).thenReturn(false);
        UserAccDbAccess userAccDbAccess = new UserAccDbAccess(mocked_SessionTracker, mocked_Database);
    }

    @Test(expected = DbBuildException.class)
    public void constructor_failed_db_build() throws Exception {
        when(mocked_Database.connect()).thenReturn(true);
        when(mocked_Database.checkUserAccDB()).thenThrow(new DbEmptyException(""));
        when(mocked_Database.setupUserAccDB()).thenReturn(false);
        UserAccDbAccess userAccDbAccess = new UserAccDbAccess(mocked_SessionTracker, mocked_Database);
    }

    @Test
    public void constructor_no_fails() throws Exception {
        when(mocked_Database.connect()).thenReturn(true);
        when(mocked_Database.checkUserAccDB()).thenReturn(true);
        when(mocked_Database.setupUserAccDB()).thenReturn(true);
        UserAccDbAccess userAccDbAccess = new UserAccDbAccess(mocked_SessionTracker, mocked_Database);
        Assert.assertTrue(true); //No exception thrown if it gets here!
    }

    @org.junit.Test
    public void checkValidity() throws Exception {
        Date date = new Date();
        when(mocked_Database.connect()).thenReturn(true);
        when(mocked_Database.checkUserAccDB()).thenReturn(true);
        when(mocked_Database.setupUserAccDB()).thenReturn(true);
        UserAccDbAccess userAccDbAccess = new UserAccDbAccess(mocked_SessionTracker, mocked_Database);
        userAccDbAccess.checkValidity("0001", date);
        Assert.assertTrue(true); //No exception thrown
    }

    @org.junit.Test(expected = SessionExpiredException.class)
    public void checkValidity_fail_expired() throws Exception {
        Date date = new Date();
        when(mocked_Database.connect()).thenReturn(true);
        when(mocked_Database.checkUserAccDB()).thenReturn(true);
        when(mocked_Database.setupUserAccDB()).thenReturn(true);
        doThrow(SessionExpiredException.class).when(mocked_SessionTracker).checkValidity("0001", date);
        UserAccDbAccess userAccDbAccess = new UserAccDbAccess(mocked_SessionTracker, mocked_Database);
        userAccDbAccess.checkValidity("0001", date);
    }

    @org.junit.Test(expected = SessionCorruptedException.class)
    public void checkValidity_fail_corrupted() throws Exception {
        Date date = new Date();
        when(mocked_Database.connect()).thenReturn(true);
        when(mocked_Database.checkUserAccDB()).thenReturn(true);
        when(mocked_Database.setupUserAccDB()).thenReturn(true);
        doThrow(SessionCorruptedException.class).when(mocked_SessionTracker).checkValidity("0001", date);
        UserAccDbAccess userAccDbAccess = new UserAccDbAccess(mocked_SessionTracker, mocked_Database);
        userAccDbAccess.checkValidity("0001", date);
    }

    @org.junit.Test(expected = SessionInvalidException.class)
    public void checkValidity_fail_invalid() throws Exception {
        Date date = new Date();
        when(mocked_Database.connect()).thenReturn(true);
        when(mocked_Database.checkUserAccDB()).thenReturn(true);
        when(mocked_Database.setupUserAccDB()).thenReturn(true);
        doThrow(SessionInvalidException.class).when(mocked_SessionTracker).checkValidity("0001", date);
        UserAccDbAccess userAccDbAccess = new UserAccDbAccess(mocked_SessionTracker, mocked_Database);
        userAccDbAccess.checkValidity("0001", date);
    }

    @org.junit.Test
    public void openSession_valid() throws Exception {
        Date date = Date.from(Instant.now());
        when(mocked_Database.connect()).thenReturn(true);
        when(mocked_Database.checkUserAccDB()).thenReturn(true);
        when(mocked_Database.setupUserAccDB()).thenReturn(true);
        doNothing().when(mocked_SessionTracker).addSession("0001", date, SessionType.USER, 0);
        UserAccDbAccess userAccDbAccess = new UserAccDbAccess(mocked_SessionTracker, mocked_Database);
        userAccDbAccess.openSession("0001", date, SessionType.USER, 0);
        Assert.assertTrue(true); //No exception thrown if it gets here!
    }

    @org.junit.Test(expected = SessionException.class)
    public void openSession_fail() throws Exception {
        Date date = Date.from(Instant.now());
        when(mocked_Database.connect()).thenReturn(true);
        when(mocked_Database.checkUserAccDB()).thenReturn(true);
        when(mocked_Database.setupUserAccDB()).thenReturn(true);
        doThrow(SessionException.class).when(mocked_SessionTracker).addSession("0001", date, SessionType.USER, 0);
        UserAccDbAccess userAccDbAccess = new UserAccDbAccess(mocked_SessionTracker, mocked_Database);
        userAccDbAccess.openSession("0001", date, SessionType.USER, 0);
    }

    @org.junit.Test
    public void closeSession_valid() throws Exception {
        when(mocked_Database.connect()).thenReturn(true);
        when(mocked_Database.checkUserAccDB()).thenReturn(true);
        when(mocked_Database.setupUserAccDB()).thenReturn(true);
        doNothing().when(mocked_SessionTracker).removeSession("0001");
        UserAccDbAccess userAccDbAccess = new UserAccDbAccess(mocked_SessionTracker, mocked_Database);
        userAccDbAccess.closeSession("0001");
    }

    @org.junit.Test(expected = SessionInvalidException.class)
    public void closeSession_invalid() throws Exception {
        when(mocked_Database.connect()).thenReturn(true);
        when(mocked_Database.checkUserAccDB()).thenReturn(true);
        when(mocked_Database.setupUserAccDB()).thenReturn(true);
        doThrow(SessionInvalidException.class).when(mocked_SessionTracker).removeSession("0001");
        UserAccDbAccess userAccDbAccess = new UserAccDbAccess(mocked_SessionTracker, mocked_Database);
        userAccDbAccess.closeSession("0001");
    }
}