package controllers.admin;

import com.typesafe.config.Config;
import controllers.WiFreeController;
import controllers.routes;
import models.Portal;
import org.pac4j.core.profile.CommonProfile;
import play.data.Form;
import play.mvc.Http;
import play.mvc.Result;
import services.PortalAndLoginOptionsService;
import views.dto.PortalOptionsView;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PortalSettingsController extends WiFreeController {

    @Inject
    PortalAndLoginOptionsService portalAndLoginOptionsService;

    @Inject
    Config config;
    
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
        
        if (files.isEmpty()) {
            flash("Error", "Debe cargar al menos una imagen");
        }

        if (portalOptions.getLoginMethods().isEmpty()) {
            flash("Error", "Debe elegir al menos un modo de autenticación");
        }
        
        CommonProfile currentProfile = getCurrentProfile();

        Portal portal = portalAndLoginOptionsService.savePortalOptions(currentProfile, portalOptions, portalOptions.getPortalId(), files);
        // refresh profile portal
        currentProfile.removeAttribute("portal");
        currentProfile.addAttribute("portal", portal);
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
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();

        File file = filePart.getFile();
        String extension = filePart.getContentType().split("/")[1];
        String sharedFolder = config.getString("images.path");
        
        String internalFolder = currentPath + "/public/img/client/";
        
        Path sharedFolderPath = Paths.get(sharedFolder);
        Path internalFolderPath = Paths.get(internalFolder);
        
        String fileName = "image_" + portalOptions.getPortalId() + "_" + i + "." + extension;

        try {
            if (Files.notExists(sharedFolderPath)){
                Files.createDirectory(sharedFolderPath);
            }
            if (Files.notExists(internalFolderPath)){
                Files.createDirectory(internalFolderPath);
            }
            Files.copy(file.toPath(), Paths.get(internalFolder + fileName), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(file.toPath(), Paths.get(sharedFolder + fileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new File(internalFolder + fileName);
    }
}
