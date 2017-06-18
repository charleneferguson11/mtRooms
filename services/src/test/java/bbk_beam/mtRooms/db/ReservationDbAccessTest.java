package bbk_beam.mtRooms.db;

import bbk_beam.mtRooms.db.database.Database;
import bbk_beam.mtRooms.db.exception.DbBuildException;
import bbk_beam.mtRooms.db.exception.EmptyDatabaseException;
import bbk_beam.mtRooms.db.exception.InvalidSessionException;
import bbk_beam.mtRooms.db.exception.SessionExpiredException;
import bbk_beam.mtRooms.db.session.SessionTracker;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReservationDbAccessTest {
    private Database mocked_Database;
    private SessionTracker mocked_SessionTracker;

    @Before
    public void setUp() throws Exception {
        mocked_Database = mock(Database.class);
        mocked_SessionTracker = mock(SessionTracker.class);
    }

    @After
    public void tearDown() throws Exception {
        mocked_Database = null;
        mocked_SessionTracker = null;
    }

    @Test(expected = SQLException.class)
    public void constructor_failed_db_connect() throws Exception {
        when(mocked_Database.connect()).thenReturn(false);
        ReservationDbAccess reservation_access = new ReservationDbAccess(mocked_SessionTracker, mocked_Database);
    }

    @Test(expected = DbBuildException.class)
    public void constructor_failed_db_check() throws Exception {
        when(mocked_Database.connect()).thenReturn(true);
        when(mocked_Database.checkReservationDB()).thenReturn(false);
        ReservationDbAccess reservation_access = new ReservationDbAccess(mocked_SessionTracker, mocked_Database);
    }

    @Test(expected = DbBuildException.class)
    public void constructor_failed_db_build() throws Exception {
        when(mocked_Database.connect()).thenReturn(true);
        when(mocked_Database.checkReservationDB()).thenThrow(new EmptyDatabaseException(""));
        when(mocked_Database.setupReservationDB()).thenReturn(false);
        ReservationDbAccess reservation_access = new ReservationDbAccess(mocked_SessionTracker, mocked_Database);
    }

    @Test
    public void constructor_no_fails() throws Exception {
        when(mocked_Database.connect()).thenReturn(true);
        when(mocked_Database.checkReservationDB()).thenReturn(true);
        ReservationDbAccess reservation_access = new ReservationDbAccess(mocked_SessionTracker, mocked_Database);
        Assert.assertTrue(true); //No exception thrown if it gets here!
    }

    @Test(expected = InvalidSessionException.class)
    public void queryDB_with_invalid_session() throws Exception {
        //Constructor
        when(mocked_Database.connect()).thenReturn(true);
        when(mocked_Database.checkReservationDB()).thenReturn(true);
        ReservationDbAccess reservation_access = new ReservationDbAccess(mocked_SessionTracker, mocked_Database);
        //Session Tracker
        when(mocked_SessionTracker.isValid("test_session_0001")).thenThrow(new InvalidSessionException(""));
        //Test
        reservation_access.queryDB("test_session_0001", "SELECT * FROM sometable;");
    }

    @Test(expected = SessionExpiredException.class)
    public void queryDB_with_expired_session() throws Exception {
        //Constructor
        when(mocked_Database.connect()).thenReturn(true);
        when(mocked_Database.checkReservationDB()).thenReturn(true);
        ReservationDbAccess reservation_access = new ReservationDbAccess(mocked_SessionTracker, mocked_Database);
        //Session Tracker
        when(mocked_SessionTracker.isValid("test_session_0001")).thenReturn(false);
        //Test
        reservation_access.queryDB("test_session_0001", "SELECT * FROM sometable;");
    }

    @Test
    public void queryDB() throws Exception {
        //Constructor
        when(mocked_Database.connect()).thenReturn(true);
        when(mocked_Database.checkReservationDB()).thenReturn(true);
        ReservationDbAccess reservation_access = new ReservationDbAccess(mocked_SessionTracker, mocked_Database);
        //Session Tracker
        when(mocked_SessionTracker.isValid("test_session_0001")).thenReturn(true);

    }
}