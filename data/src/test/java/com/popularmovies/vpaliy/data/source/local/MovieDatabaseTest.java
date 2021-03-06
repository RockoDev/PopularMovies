package com.popularmovies.vpaliy.data.source.local;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Build;
import android.provider.BaseColumns;
import android.util.Log;

import com.google.android.apps.common.testing.accessibility.framework.proto.FrameworkProtos;
import com.google.gson.reflect.TypeToken;
import com.popularmovies.vpaliy.data.BuildConfig;
import com.popularmovies.vpaliy.data.configuration.ImageQualityConfiguration;
import com.popularmovies.vpaliy.data.entity.BackdropImage;
import com.popularmovies.vpaliy.data.entity.Genre;
import com.popularmovies.vpaliy.data.entity.Movie;
import com.popularmovies.vpaliy.data.source.DataSourceTestUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static com.popularmovies.vpaliy.data.source.DataSourceTestUtils.FAKE_MOVIE_ID;
import static com.popularmovies.vpaliy.data.source.DataSourceTestUtils.FAKE_RELEASE_DATE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static com.popularmovies.vpaliy.data.source.DataSourceTestUtils.FAKE_POSTER_PATH;
import static com.popularmovies.vpaliy.data.source.DataSourceTestUtils.FAKE_BACKDROP_PATH;
import static com.popularmovies.vpaliy.data.source.DataSourceTestUtils.FAKE_HOMEPAGE;
import static com.popularmovies.vpaliy.data.source.DataSourceTestUtils.FAKE_IS_FAVORITE;
import static com.popularmovies.vpaliy.data.source.DataSourceTestUtils.FAKE_OVERVIEW;
import static com.popularmovies.vpaliy.data.source.DataSourceTestUtils.FAKE_TITLE;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = Build.VERSION_CODES.LOLLIPOP)
public class MovieDatabaseTest {

    /*
    private MovieSQLHelper sqlHelper;

    private static final String MOVIE_SELECTION_BY_ID=
            MoviesContract.MovieEntry.TABLE_NAME+"."+ MoviesContract.MovieEntry._ID+"=?";

    private static final String MOST_RATED_SELECTION_BY_ID=
            MoviesContract.MostRatedEntry.TABLE_NAME+"."+ MoviesContract.MovieEntry.MOVIE_ID+"=?";

    private static final String MOST_POPULAR_SELECTION_BY_ID=
            MoviesContract.MostPopularEntry.TABLE_NAME+"."+ MoviesContract.MovieEntry.MOVIE_ID+"=?";

    private static final String FAVORITE_SELECTION_BY_ID=
            MoviesContract.FavoriteEntry.TABLE_NAME+"."+ MoviesContract.MovieEntry.MOVIE_ID+"=?";

    private  ImageQualityConfiguration qualityConfiguration;

    @Before
    public void setUp(){
        sqlHelper=new MovieSQLHelper(RuntimeEnvironment.application);
        qualityConfiguration=new ImageQualityConfiguration(RuntimeEnvironment.application);
        clearDatabase();
        createDatabase();


    }

    @After
    public void  tearDown() {
        clearDatabase();
    }


    @Test
    public void testMovieQuery(){
        SQLiteDatabase database=sqlHelper.getWritableDatabase();

        final Movies movie=DataSourceTestUtils.provideFakeMovie();
        final Movies secondMovie=DataSourceTestUtils.provideFakeMovie();
        database.insert(MoviesContract.MovieEntry.TABLE_NAME,null,
                DataSourceTestUtils.provideFakeValues(movie));
        secondMovie.setMovieId(1234);
        database.insert(MoviesContract.MovieEntry.TABLE_NAME,null,
                DataSourceTestUtils.provideFakeValues(secondMovie));

        //query all
        Cursor cursor=database.query(MoviesContract.MovieEntry.TABLE_NAME,
                null,null,null,null,null,null);
        assertThat(cursor.moveToFirst(),is(true));
        assertThatMovieIsEqualToCursor(cursor,movie,2,false);
        assertThat(cursor.moveToNext(),is(true));
        assertThatMovieIsEqualToCursor(cursor,secondMovie,2,true);

        //make a query by id
        String[] selectionArgs={Integer.toString(secondMovie.getMovieId())};


        cursor=database.query(MoviesContract.MovieEntry.TABLE_NAME,null,MOVIE_SELECTION_BY_ID,selectionArgs,null,null,null);
        assertThat(cursor.moveToFirst(),is(true));
        assertThatMovieIsEqualToCursor(cursor,secondMovie,1,true);


    }


    @Test
    public void testTopRatedMoviesQuery(){
        final SQLiteDatabase database=sqlHelper.getWritableDatabase();
        final Movies movie= DataSourceTestUtils.provideFakeMovie();
        final Movies secondMovie=DataSourceTestUtils.provideFakeMovie();
        database.insert(MoviesContract.MovieEntry.TABLE_NAME,null,DataSourceTestUtils.provideFakeValues(movie));


        ContentValues values=new ContentValues();
        values.put(MoviesContract.MostRatedEntry._ID,movie.getMovieId());
        values.put(MoviesContract.MovieEntry.MOVIE_ID,movie.getMovieId());

        database.insert(MoviesContract.MostRatedEntry.TABLE_NAME,null,values);

        secondMovie.setMovieId(1234);
        database.insert(MoviesContract.MovieEntry.TABLE_NAME,null,DataSourceTestUtils.provideFakeValues(secondMovie));
        values.put(MoviesContract.MostRatedEntry._ID,secondMovie.getMovieId());
        values.put(MoviesContract.MovieEntry.MOVIE_ID,secondMovie.getMovieId());

        database.insert(MoviesContract.MostRatedEntry.TABLE_NAME,null,values);

        //query all entries
        Cursor cursor=DatabaseUtils.fetchFromMovieTable(MoviesContract.MostRatedEntry.TABLE_NAME,
                null,null,null,null,sqlHelper);

        assertThat(cursor.moveToFirst(),is(true));
        assertThatMovieIsEqualToCursor(cursor,movie,2,false);
        assertThat(cursor.moveToNext(),is(true));
        assertThatMovieIsEqualToCursor(cursor,secondMovie,2,true);

        //query the first entry by its id
        String[] selectionArgs={Integer.toString(movie.getMovieId())};

        cursor=DatabaseUtils.fetchFromMovieTable(MoviesContract.MostRatedEntry.TABLE_NAME,
                null,MOST_RATED_SELECTION_BY_ID,selectionArgs,null,sqlHelper);

        assertThat(cursor.moveToFirst(),is(true));
        assertThatMovieIsEqualToCursor(cursor,movie,1,true);


    }

    @Test
    public void testMostPopularMoviesQuery(){
        final SQLiteDatabase database=sqlHelper.getWritableDatabase();
        final Movies movie= DataSourceTestUtils.provideFakeMovie();
        final Movies secondMovie=DataSourceTestUtils.provideFakeMovie();
        database.insert(MoviesContract.MovieEntry.TABLE_NAME,null,DataSourceTestUtils.provideFakeValues(movie));


        ContentValues values=new ContentValues();
        values.put(MoviesContract.MostPopularEntry._ID,movie.getMovieId());
        values.put(MoviesContract.MovieEntry.MOVIE_ID,movie.getMovieId());

        database.insert(MoviesContract.MostPopularEntry.TABLE_NAME,null,values);

        secondMovie.setMovieId(1234);
        database.insert(MoviesContract.MovieEntry.TABLE_NAME,null,DataSourceTestUtils.provideFakeValues(secondMovie));
        values.put(MoviesContract.MostPopularEntry._ID,secondMovie.getMovieId());
        values.put(MoviesContract.MovieEntry.MOVIE_ID,secondMovie.getMovieId());

        database.insert(MoviesContract.MostPopularEntry.TABLE_NAME,null,values);

        //query all entries
        Cursor cursor=DatabaseUtils.fetchFromMovieTable(MoviesContract.MostPopularEntry.TABLE_NAME,
                null,null,null,null,sqlHelper);

        assertThat(cursor.moveToFirst(),is(true));
        assertThatMovieIsEqualToCursor(cursor,movie,2,false);
        assertThat(cursor.moveToNext(),is(true));
        assertThatMovieIsEqualToCursor(cursor,secondMovie,2,true);

        //query the first entry by its id
        String[] selectionArgs={Integer.toString(movie.getMovieId())};

        cursor=DatabaseUtils.fetchFromMovieTable(MoviesContract.MostPopularEntry.TABLE_NAME,
                null,MOST_POPULAR_SELECTION_BY_ID,selectionArgs,null,sqlHelper);

        assertThat(cursor.moveToFirst(),is(true));
        assertThatMovieIsEqualToCursor(cursor,movie,1,true);
    }

    @Test
    public void testFavoriteQuery(){
        final SQLiteDatabase database=sqlHelper.getWritableDatabase();
        final Movies firstMovie=DataSourceTestUtils.provideFakeMovie();
        final Movies secondMovie=DataSourceTestUtils.provideFakeMovie();
        secondMovie.setMovieId(1);
        final Movies thirdMovie=DataSourceTestUtils.provideFakeMovie();
        thirdMovie.setMovieId(0);

        final Movies[] movies={firstMovie,secondMovie,thirdMovie};
        for(Movies movie:movies) {
            database.insert(MoviesContract.MovieEntry.TABLE_NAME,null,
                    DataSourceTestUtils.provideFakeValues(movie));

        }

        ContentValues values=new ContentValues();
        values.put(MoviesContract.FavoriteEntry._ID,firstMovie.getMovieId());
        values.put(MoviesContract.MovieEntry.MOVIE_ID,firstMovie.getMovieId());
        database.insert(MoviesContract.FavoriteEntry.TABLE_NAME,null,values);

        values.put(MoviesContract.FavoriteEntry._ID,secondMovie.getMovieId());
        values.put(MoviesContract.MovieEntry.MOVIE_ID,secondMovie.getMovieId());
        database.insert(MoviesContract.FavoriteEntry.TABLE_NAME,null,values);

        values.put(MoviesContract.FavoriteEntry._ID,thirdMovie.getMovieId());
        values.put(MoviesContract.MovieEntry.MOVIE_ID,thirdMovie.getMovieId());
        database.insert(MoviesContract.FavoriteEntry.TABLE_NAME,null,values);


        //test favorite query
        String order=MoviesContract.MovieEntry._ID+" ASC";
        Cursor cursor=DatabaseUtils.fetchFromMovieTable(MoviesContract.FavoriteEntry.TABLE_NAME,
                null,null,null,order,sqlHelper);
        assertThat(cursor.moveToFirst(),is(true));


        //Cursor reads rows in the ascending order by its id
        assertThatMovieIsEqualToCursor(cursor,thirdMovie,3,false);
        assertThat(cursor.moveToNext(),is(true));
        assertThatMovieIsEqualToCursor(cursor,secondMovie,3,false);
        assertThat(cursor.moveToNext(),is(true));
        assertThatMovieIsEqualToCursor(cursor,firstMovie,3,true);
    }


    @Test
    public void testMovieInsertAndReplace(){
        final SQLiteDatabase database=sqlHelper.getWritableDatabase();
        final Movies movie= DataSourceTestUtils.provideFakeMovie();
        database.insert(MoviesContract.MovieEntry.TABLE_NAME,null,DataSourceTestUtils.provideFakeValues(movie));
        Cursor cursor=database.query(MoviesContract.MovieEntry.TABLE_NAME,null,null,null,null,null,null);
        assertThat(cursor.moveToFirst(),is(true));
        assertThatMovieIsEqualToCursor(cursor,movie,1,true);


        //change the data in the same row
        movie.setPosterPath(null);
        movie.setFavorite(false);
        movie.setVoteCount(0);
        movie.setVoteAverage(0);
        movie.setBackdropPath(null);
        movie.setHomepage(null);
        movie.setReleaseDate(null);
        movie.setTitle(null);
        movie.setOverview(null);

        database.insertWithOnConflict(MoviesContract.MovieEntry.TABLE_NAME,null,
                DataSourceTestUtils.provideFakeValues(movie),SQLiteDatabase.CONFLICT_REPLACE);

        cursor=database.query(MoviesContract.MovieEntry.TABLE_NAME,null,null,null,null,null,null);
        assertThat(cursor.moveToFirst(),is(true));
        assertThatMovieIsEqualToCursor(cursor,movie,1,true);


    }


    private void assertThatMovieIsEqualToCursor(Cursor cursor, Movies movie, int count, boolean close){
        assertThat(cursor.getCount(), is(count));
        assertThat(cursor.getInt(cursor.getColumnIndex(_ID)), is(movie.getMovieId()));
        assertThat(cursor.getString(cursor.getColumnIndex(COLUMN_ORIGINAL_TITLE)),is(movie.getOriginalTitle()));
        assertThat(cursor.getString(cursor.getColumnIndex(COLUMN_OVERVIEW)), is(movie.getOverview()));
        assertThat(cursor.getString(cursor.getColumnIndex(COLUMN_RELEASE_DATE)), is(movie.getReleaseDate()));
        assertThat(cursor.getString(cursor.getColumnIndex(COLUMN_POSTER_PATH)), is(movie.getPosterPath()));
        assertThat(cursor.getDouble(cursor.getColumnIndex(COLUMN_POPULARITY)), is(movie.getPopularity()));
        assertThat(cursor.getLong(cursor.getColumnIndex(COLUMN_BUDGET)), is(movie.getBudget()));
        assertThat(cursor.getInt(cursor.getColumnIndex(COLUMN_RUNTIME)), is(movie.getRuntime()));
        assertThat(cursor.getLong(cursor.getColumnIndex(COLUMN_REVENUE)), is(movie.getRevenue()));
        assertThat(cursor.getString(cursor.getColumnIndex(COLUMN_HOME_PAGE)), is(movie.getHomepage()));
        assertThat(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)), is(movie.getTitle()));
        assertThat(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_FAVORITE))==1, is(movie.isFavorite()));
        assertThat(cursor.getDouble(cursor.getColumnIndex(COLUMN_AVERAGE_VOTE)), is(movie.getVoteAverage()));
        assertThat(cursor.getLong(cursor.getColumnIndex(COLUMN_VOTE_COUNT)), is(movie.getVoteCount()));
        assertThat(cursor.getString(cursor.getColumnIndex(COLUMN_BACKDROP_PATH)),is(movie.getBackdrop_path()));

        String jsonString=cursor.getString(cursor.getColumnIndex(COLUMN_MOVIE_BACKDROPS));
        Type type=new TypeToken<ArrayList<BackdropImage>>(){}.getType();

        List<BackdropImage> backdrops=DatabaseUtils.convertFromJsonString(jsonString,type);

        assertThat(backdrops.size(),is(movie.getBackdropImages().size()));
        Assert.assertArrayEquals(BackdropImage.convert(backdrops,qualityConfiguration).toArray(new String[backdrops.size()]),
                BackdropImage.convert(movie.getBackdropImages(),qualityConfiguration).toArray(new String[backdrops.size()]));

        jsonString=cursor.getString(cursor.getColumnIndex((COLUMN_GENRES)));
        type=new TypeToken<ArrayList<Genre>>(){}.getType();
        List<Genre> genreList=DatabaseUtils.convertFromJsonString(jsonString,type);

        assertThat(genreList.size(),is(movie.getGenres().size()));
        Assert.assertArrayEquals(Genre.convert(genreList).toArray(new String[genreList.size()]),
                Genre.convert(movie.getGenres()).toArray(new String[genreList.size()]));

        if(close) {
            assertThat(cursor.isClosed(), is(false));
            cursor.close();
            assertThat(cursor.isClosed(), is(true));
        }
    }


    @Test
    public void testMovieUpdate(){
        final SQLiteDatabase database=sqlHelper.getWritableDatabase();
        final Movies movie= DataSourceTestUtils.provideFakeMovie();
        database.insert(MoviesContract.MovieEntry.TABLE_NAME,null,DataSourceTestUtils.provideFakeValues(movie));
        movie.setTitle(FAKE_TITLE+FAKE_TITLE);
        movie.setReleaseDate(FAKE_RELEASE_DATE+FAKE_RELEASE_DATE);
        movie.setOverview(FAKE_OVERVIEW+FAKE_OVERVIEW);
        movie.setHomepage(FAKE_HOMEPAGE+FAKE_HOMEPAGE);
        movie.setBackdropPath(FAKE_BACKDROP_PATH+FAKE_BACKDROP_PATH);
        movie.setFavorite(!FAKE_IS_FAVORITE);

        final int rowsUpdated=database.update(MoviesContract.MovieEntry.TABLE_NAME,DataSourceTestUtils.provideFakeValues(movie),null,null);
        assertThat(rowsUpdated,is(1));
        String[] selectionArgs = new String[]{Long.toString(movie.getMovieId())};
        Cursor cursor=database.query(MoviesContract.MovieEntry.TABLE_NAME,
                null,MOVIE_SELECTION_BY_ID,selectionArgs,null,null,null);
        assertThat(cursor.moveToFirst(),is(true));
        assertThatMovieIsEqualToCursor(cursor,movie,1,true);

    }


    @Test
    public void testMovieDelete(){
        final SQLiteDatabase database=sqlHelper.getWritableDatabase();
        final Movies movie= DataSourceTestUtils.provideFakeMovie();
        database.insert(MoviesContract.MovieEntry.TABLE_NAME,null,DataSourceTestUtils.provideFakeValues(movie));

        String[] selectionArgs = new String[]{Long.toString(movie.getMovieId())};
        final int rowsDeleted=database.delete(MoviesContract.MovieEntry.TABLE_NAME,MOVIE_SELECTION_BY_ID,selectionArgs);
        assertThat(rowsDeleted,is(1));

        Cursor cursor=database.query(MoviesContract.MovieEntry.TABLE_NAME,
                null,MOVIE_SELECTION_BY_ID,selectionArgs,null,null,null);
        assertThat(cursor.getCount(),is(0));
        if(!cursor.isClosed()) cursor.close();

    }

    @Test
    public void testMostPopularDelete(){
        testDeleteInAdditionalTable(MoviesContract.MostPopularEntry.TABLE_NAME,MOST_POPULAR_SELECTION_BY_ID);
    }

    @Test
    public void testMostRatedDelete(){
        testDeleteInAdditionalTable(MoviesContract.MostRatedEntry.TABLE_NAME,MOST_RATED_SELECTION_BY_ID);
    }


    @Test
    public void testFavoriteDelete(){
        testDeleteInAdditionalTable(MoviesContract.FavoriteEntry.TABLE_NAME,FAVORITE_SELECTION_BY_ID);
    }


    private void testDeleteInAdditionalTable(String tableName, String selectionById){
        final SQLiteDatabase database=sqlHelper.getWritableDatabase();
        final Movies movie= DataSourceTestUtils.provideFakeMovie();
        database.insert(MoviesContract.MovieEntry.TABLE_NAME,null,DataSourceTestUtils.provideFakeValues(movie));
        String[] selectionArgs = new String[]{Long.toString(movie.getMovieId())};


        ContentValues values=new ContentValues();
        values.put(BaseColumns._ID,movie.getMovieId());
        values.put(MoviesContract.MovieEntry.MOVIE_ID,movie.getMovieId());
        database.insert(tableName,null,values);

        Cursor cursor=DatabaseUtils.fetchFromMovieTable(tableName,null,selectionById, selectionArgs,null,sqlHelper);
        assertThat(cursor.getCount(),is(1));
        if(!cursor.isClosed()) cursor.close();

        final int rowsDeleted=database.delete(tableName,selectionById,selectionArgs);
        assertThat(rowsDeleted,is(1));

        cursor=DatabaseUtils.fetchFromMovieTable(tableName,null,selectionById, selectionArgs,null,sqlHelper);
        assertThat(cursor.getCount(),is(0));
        if(!cursor.isClosed()) cursor.close();
    }


    @Test
    public void testMostPopularMoviesInsert(){
        testInsertInAdditionalTable(MoviesContract.MostPopularEntry.TABLE_NAME);
    }

    @Test
    public void testTopRatedMoviesInsert(){
      testInsertInAdditionalTable(MoviesContract.MostRatedEntry.TABLE_NAME);
    }

    @Test
    public void testFavoriteInsert(){
        testInsertInAdditionalTable(MoviesContract.FavoriteEntry.TABLE_NAME);
    }


    private void testInsertInAdditionalTable(String tableName){
        final SQLiteDatabase database=sqlHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(BaseColumns._ID,FAKE_MOVIE_ID);
        values.put(MoviesContract.MovieEntry.MOVIE_ID,FAKE_MOVIE_ID);

        database.insert(tableName,null,values);

        Cursor cursor=database.query(tableName,null,null,null,null,null,null);

        assertThat(cursor.getCount(),is(1));
        assertThat(cursor.moveToFirst(),is(true));
        assertThat(cursor.getInt(cursor.getColumnIndex(MoviesContract.MovieEntry.MOVIE_ID)),is(FAKE_MOVIE_ID));

        if(!cursor.isClosed()) cursor.close();
    }



    private void createDatabase(){
        if(sqlHelper!=null) {
            SQLiteDatabase db = sqlHelper.getWritableDatabase();
            db.execSQL(MoviesContract.MovieEntry.SQL_CREATE_TABLE);
            db.execSQL(MoviesContract.MostPopularEntry.SQL_CREATE_TABLE);
            db.execSQL(MoviesContract.MostRatedEntry.SQL_CREATE_TABLE);
        }
    }

    private void clearDatabase(){
        if(sqlHelper!=null) {
            SQLiteDatabase db = sqlHelper.getWritableDatabase();
            db.execSQL(MoviesContract.MovieEntry.SQL_DROP_IF_EXISTS);
            db.execSQL(MoviesContract.MostPopularEntry.SQL_DROP_IF_EXISTS);
            db.execSQL(MoviesContract.MostRatedEntry.SQL_DROP_IF_EXISTS);
        }
    } */
}
