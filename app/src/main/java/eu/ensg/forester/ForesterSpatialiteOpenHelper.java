package eu.ensg.forester;

import android.content.Context;

import java.io.IOException;

import eu.ensg.spatialite.SpatialiteDatabase;
import eu.ensg.spatialite.SpatialiteOpenHelper;
import jsqlite.*;
import jsqlite.Exception;

/**
 * Created by prof on 25/02/16.
 */
public class ForesterSpatialiteOpenHelper extends SpatialiteOpenHelper {

    public static final String DB_FILE_NAME = "Spatialite.sqlite";
    public static final int VERSION = 1;
    public static final int GPS_SRID = 4326;

    public ForesterSpatialiteOpenHelper(Context context) throws Exception, IOException {
        super(context, DB_FILE_NAME, VERSION);
    }


    @Override
    public void onCreate(SpatialiteDatabase db) throws jsqlite.Exception {

        //creation table des forrestiers
        db.exec("CREATE TABLE IF NOT EXISTS Forester (\n" +
                        "ID integer PRIMARY KEY AUTOINCREMENT,\n " +
                        "FirstName string NOT NULL, \n" +
                        "LastName string NOT NULL, \n" +
                        "Serial string NULL)"

        );

        //creation table des points d'interet
        db.exec("CREATE TABLE IF NOT EXISTS PointOfInterest (\n" +
                "ID integer PRIMARY KEY AUTOINCREMENT, \n" +
                "ForesterID integer NOT NULL,\n" +
                "Name string NOT NULL, \n" +
                "Description string,\n" +
                "CONSTRAINT FK_poi_forester\n" +
                "   FOREIGN KEY (foresterID)\n" +
                "   REFERENCES forester (id)\n" +
                ");");

        db.exec("SELECT AddGeometryColumn ('PointOfInterest',\n" +
                        "'Position', 4326, 'POINT', 'XY',\n" +
                        "0)"
        );

        //getDatabase().exec("CREATE INDEX index_poi ON PointOfInterest (foresterID)");

        db.exec("SELECT AddGeometryColumn('PointOfInterest', 'Position', " + GPS_SRID + ", 'POINT', 'XY', 1);");

        // table district
        db.exec("CREATE TABLE IF NOT EXISTS District (\n" +
                "ID integer PRIMARY KEY AUTOINCREMENT, \n" +
                "ForesterID integer NOT NULL,\n" +
                "Name string NOT NULL, \n" +
                "Description string,\n" +
                "CONSTRAINT FK_district_forester\n" +
                "   FOREIGN KEY (foresterID)\n" +
                "   REFERENCES forester (id)\n" +
                ")");

        db.exec("SELECT AddGeometryColumn('Disctrict',\n" +
                        "'Area', 4326, 'POLYGON', 'XY', 0"
        );

        db.exec("SELECT AddGeometryColumn('District', 'Area', " + GPS_SRID + ", 'POLYGON', 'XY', 1);");

        //getDatabase().exec("CREATE INDEX index_sector ON District (foresterID)");

    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) throws Exception {

    }
}

