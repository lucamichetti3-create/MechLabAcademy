package it.lucamichetti.mechlabacademy.data
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import it.lucamichetti.mechlabacademy.data.local.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
@RunWith(AndroidJUnit4::class) class AcademyDaoTest{
 private lateinit var db:AcademyDatabase
 @Before fun setup(){db=Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext<Context>(),AcademyDatabase::class.java).allowMainThreadQueries().build()}
 @After fun close(){db.close()}
 @Test fun insertsAndReadsSubject()=runBlocking{val d=db.academyDao();d.insertSubjects(listOf(SubjectEntity("m","M","Meccanica","TECHNICAL","[3]",1,"test")));assertEquals(1,d.observeSubjects().first().size)}
}
