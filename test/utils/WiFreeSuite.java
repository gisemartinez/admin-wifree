package utils;

import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import io.ebean.Ebean;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import play.Application;
import play.Environment;
import play.mvc.Http;
import play.test.Helpers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.apache.commons.io.FileUtils.getFile;

public class WiFreeSuite {

    public static Application app;
    public static String createDdl = "";
    public static String dropDdl = "";

    @BeforeClass
    public static void startApp() throws IOException {
        app = Helpers.fakeApplication(Helpers.inMemoryDatabase());
        Helpers.start(app);

        // Reading the evolution file
        String evolutionContent = FileUtils.readFileToString(
                Environment.simple().getFile("conf/evolutions/default/1.sql"), Charset.defaultCharset());

        // Splitting the String to get Create & Drop DDL
        String[] splittedEvolutionContent = evolutionContent.split("# --- !Ups");
        String[] upsDowns = splittedEvolutionContent[1].split("# --- !Downs");
        createDdl = upsDowns[0];
        dropDdl = upsDowns[1];
    }

    @AfterClass
    public static void stopApp() {
        Helpers.stop(app);
    }

    @Before
    public void createCleanDb() {
        Ebean.execute(Ebean.createCallableSql(dropDdl));
        Ebean.execute(Ebean.createCallableSql(createDdl));
    }

    public Http.MultipartFormData.Part<Source<ByteString, ?>> multipart(String fileName) {
        String resource = getClass().getClassLoader().getResource(fileName).getFile();
        File file = getFile(resource);

        Http.MultipartFormData.Part<Source<ByteString, ?>> part =
                new Http.MultipartFormData.FilePart<>(
                        "image",
                        fileName,
                        "image/jpeg",
                        FileIO.fromPath(file.toPath()));

        return part;
    }

    public Http.MultipartFormData.Part<Source<ByteString, ?>> multipartFrom(String key, String value) {
        Http.MultipartFormData.Part<Source<ByteString, ?>> part =
                new Http.MultipartFormData.DataPart(
                        key,
                        value);

        return part;
    }
}
