package controllers.admin;

import com.typesafe.config.Config;
import controllers.WiFreeController;
import controllers.routes;
import play.data.Form;
import play.mvc.Http;
import play.mvc.Result;
import services.PortalAndLoginOptionsService;
import views.dto.PortalOptionsView;
import views.dto.SocialKeysView;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LoginOptionsController extends WiFreeController {

    @Inject
    PortalAndLoginOptionsService portalAndLoginOptionsService;

    @Inject
    Config config;

    public Result saveSocialKeys() {
        final Form<SocialKeysView> form = formFactory.form(SocialKeysView.class);
        final SocialKeysView socialKeys = form.bindFromRequest().get();
        portalAndLoginOptionsService.saveLoginOptions(socialKeys, portalId());
        return redirect(routes.AdminAppController.loginSettings());
    }

    public Result savePortalOptions() {
        final Form<PortalOptionsView> form = formFactory.form(PortalOptionsView.class);
        final PortalOptionsView portalOptions = form.bindFromRequest().get();

        Http.MultipartFormData<File> body = request().body().asMultipartFormData();

        List<String> existingImages = getExistingImages(body);

        List<Http.MultipartFormData.FilePart<File>> fileParts = body.getFiles()
                .stream()
                .filter(this::hasName)
                .collect(Collectors.toList());

        List<File> files = fileParts.isEmpty()
                ? existingImages.stream().map(File::new).collect(Collectors.toList())
                : IntStream.range(0, fileParts.size())
                    .mapToObj(i -> moveFile(portalOptions, fileParts, i))
                    .collect(Collectors.toList());

        portalAndLoginOptionsService.savePortalOptions(portalOptions, portalId(), files);
        return redirect(routes.AdminAppController.portalSettings());
    }

    private List<String> getExistingImages(Http.MultipartFormData<File> body) {
        return body.asFormUrlEncoded()
                .entrySet().stream()
                .filter(x -> x.getKey().startsWith("image."))
                .sorted(Map.Entry.comparingByKey())
                .map(x -> x.getValue()[0])
                .collect(Collectors.toList());
    }

    private boolean hasName(Http.MultipartFormData.FilePart<File> f) {
        return !f.getFilename().isEmpty();
    }

    private File moveFile(PortalOptionsView portalOptions, List<Http.MultipartFormData.FilePart<File>> fileParts, int i) {
        Http.MultipartFormData.FilePart<File> filePart = fileParts.get(i);
        File file = filePart.getFile();
        String extension = filePart.getContentType().split("/")[1];
        String externalPathPrefix = config.getString("images.path");
        String fileName = "image_" + portalOptions.getPortalId() + "_" + i + "." + extension;
        String newPath = "public/img/client/" + fileName;
        String externalPath = externalPathPrefix + fileName;
        try {
            Files.copy(file.toPath(), Paths.get(newPath), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(file.toPath(), Paths.get(externalPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new File(newPath);
    }

}
