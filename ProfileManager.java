package me.ancastanoev.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.*;

public class ProfileManager {

    private static final String PROFILE_DIR = "profiles";

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(ClimberProfile.class, new ClimberProfileAdapter())
            .setPrettyPrinting()
            .create();

    public static ClimberProfile loadProfile(String userKey) {
        File dir = new File(PROFILE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File profileFile = new File(dir, userKey + ".json");
        if (!profileFile.exists()) {
            return null;
        }
        try (Reader reader = new FileReader(profileFile)) {
            return gson.fromJson(reader, ClimberProfile.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveProfile(String userKey, ClimberProfile profile) {
        File dir = new File(PROFILE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File profileFile = new File(dir, userKey + ".json");
        try (Writer writer = new FileWriter(profileFile)) {
            gson.toJson(profile, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClimberProfileAdapter extends TypeAdapter<ClimberProfile> {

        @Override
        public void write(JsonWriter out, ClimberProfile profile) throws IOException {
            out.beginObject();
            out.name("firstName").value(profile.getFirstName());
            out.name("lastName").value(profile.getLastName());
            out.name("experienceLevel").value(profile.getExperienceLevel());
            out.name("contactInfo").value(profile.getContactInfo());
            out.name("profilePictureUrl").value(profile.getProfilePictureUrl());
            out.name("bio").value(profile.getBio());
            out.name("completedExpeditions");
            out.beginArray();
            for (String expedition : profile.getCompletedExpeditions()) {
                out.value(expedition);
            }
            out.endArray();
            out.endObject();
        }

        @Override
        public ClimberProfile read(JsonReader in) throws IOException {
            ClimberProfile profile = new ClimberProfile();
            in.beginObject();
            while (in.hasNext()) {
                String name = in.nextName();
                switch (name) {
                    case "firstName":
                        profile.setFirstName(in.nextString());
                        break;
                    case "lastName":
                        profile.setLastName(in.nextString());
                        break;
                    case "experienceLevel":
                        profile.setExperienceLevel(in.nextString());
                        break;
                    case "contactInfo":
                        profile.setContactInfo(in.nextString());
                        break;
                    case "profilePictureUrl":
                        profile.setProfilePictureUrl(in.nextString());
                        break;
                    case "bio":
                        profile.setBio(in.nextString());
                        break;
                    case "completedExpeditions":
                        in.beginArray();
                        while (in.hasNext()) {
                            profile.getCompletedExpeditions().add(in.nextString());
                        }
                        in.endArray();
                        break;
                    default:
                        in.skipValue();
                        break;
                }
            }
            in.endObject();
            return profile;
        }
    }
}
