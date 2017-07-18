package com.sparshik.yogicapple.utils;

import com.firebase.geofire.GeoLocation;
import com.sparshik.yogicapple.BuildConfig;

import java.util.Arrays;
import java.util.List;

/**
 * Constants class store most important strings and paths of the app
 */
public class Constants {

    public static final String FIREBASE_LOCATION_USERS = "users";
    public static final String FIREBASE_LOCATION_UID_MAPPINGS = "uidMappings";
    public static final String FIREBASE_LOCATION_PROGRAMS = "programs";
    public static final String FIREBASE_LOCATION_PROGRAM_PACKS = "programPacks";
    public static final String FIREBASE_LOCATION_PACK_APPLES = "packApples";
    public static final String FIREBASE_LOCATION_USER_OFFLINE_DOWNLOADS = "userOfflineDownloads";
    public static final String FIREBASE_LOCATION_SUPPORT_GROUPS = "supportGroups";
    public static final String FIREBASE_LOCATION_USER_SUPPORT_GROUPS = "userSupportGroups";
    public static final String FIREBASE_LOCATION_GROUP_CHATS = "groupChats";
    public static final String FIREBASE_LOCATION_GROUP_CHAT_PROFILES = "groupChatProfiles";
    public static final String FIREBASE_LOCATION_EVENTS = "events";
    public static final String FIREBASE_LOCATION_GEOFIRE = "_geofire";

    /**
     * Constants for Firebase object properties
     */

    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";
    public static final String FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED = "timestampLastChanged";
    public static final String FIREBASE_PROPERTY_CREATED_BY = "createdBy";
    public static final String FIREBASE_PROPERTY_LAST_CHANGED_BY = "lastChangedBy";
    public static final String FIREBASE_PROPERTY_USER_IS_VERIFIED = "verified";
    public static final String FIREBASE_PROPERTY_APPLE_SEQ_NUMBER = "appleSeqNumber";
    public static final String FIREBASE_PROPERTY_GROUP_NAME = "groupName";

    /**
     * Constants for Firebase Database URL
     */
    public static final String FIREBASE_URL = BuildConfig.UNIQUE_FIREBASE_ROOT_URL;
    public static final String FIREBASE_URL_USERS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USERS;
    public static final String FIREBASE_URL_PROGRAMS = FIREBASE_URL + "/" + FIREBASE_LOCATION_PROGRAMS;
    public static final String FIREBASE_URL_PROGRAM_PACKS = FIREBASE_URL + "/" + FIREBASE_LOCATION_PROGRAM_PACKS;
    public static final String FIREBASE_URL_PACK_APPLES = FIREBASE_URL + "/" + FIREBASE_LOCATION_PACK_APPLES;
    public static final String FIREBASE_URL_USER_OFFLINE_DOWNLOADS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USER_OFFLINE_DOWNLOADS;
    public static final String FIREBASE_URL_SUPPORT_GROUPS = FIREBASE_URL + "/" + FIREBASE_LOCATION_SUPPORT_GROUPS;
    public static final String FIREBASE_URL_USER_SUPPORT_GROUPS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USER_SUPPORT_GROUPS;
    public static final String FIREBASE_URL_GROUP_CHATS = FIREBASE_URL + "/" + FIREBASE_LOCATION_GROUP_CHATS;
    public static final String FIREBASE_URL_GROUP_CHAT_PROFILES = FIREBASE_URL + "/" + FIREBASE_LOCATION_GROUP_CHAT_PROFILES;
    public static final String FIREBASE_URL_EVENTS = FIREBASE_URL + "/" + FIREBASE_LOCATION_EVENTS;
    public static final String FIREBASE_URL_GEOFIRE = FIREBASE_URL + "/" + FIREBASE_LOCATION_GEOFIRE;

    /**
     * Constants for Firebase Storage URL
     */
    public static final String FIREBASE_STORAGE_URL = BuildConfig.FIREBASE_STORAGE_ROOT_URL;
    public static final String FIREBASE_STORAGE_URL_PROGRAMS = FIREBASE_STORAGE_URL + "/" + FIREBASE_LOCATION_PROGRAMS;
    public static final String FIREBASE_STORAGE_URL_PROGRAM_PACKS = FIREBASE_STORAGE_URL + "/" + FIREBASE_LOCATION_PROGRAM_PACKS;
    public static final String FIREBASE_STORAGE_URL_PACK_APPLES = FIREBASE_STORAGE_URL + "/" + FIREBASE_LOCATION_PACK_APPLES;
    public static final String FIREBASE_STORAGE_URL_SUPPORT_GROUPS = FIREBASE_STORAGE_URL + "/" + FIREBASE_LOCATION_SUPPORT_GROUPS;
    public static final String FIREBASE_STORAGE_URL_EVENTS = FIREBASE_STORAGE_URL + "/" + FIREBASE_LOCATION_EVENTS;

    /**
     * Constants for bundles, extras and shared preferences keys
     */
    public static final String KEY_LAYOUT_RESOURCE = "LAYOUT_RESOURCE";
    public static final String KEY_ENCODED_EMAIL = "ENCODED_EMAIL";
    public static final String KEY_PROVIDER_ID = "PROVIDER_ID";
    public static final String KEY_SIGNUP_EMAIL = "SIGNUP_EMAIL";
    public static final String KEY_PROGRAM_ID = "PROGRAM_ID";
    public static final String KEY_PACK_ID = "PACK_ID";
    public static final String KEY_APPLE_ID = "APPLE_ID";
    public static final String KEY_AUDIO_URL = "AUDIO_URL";
    public static final String KEY_INSTALL_ID = "INSTALL_ID";
    public static final String KEY_CURRENT_PROGRAM_ID = "CURRENT_PROGRAM_ID";
    public static final String KEY_CURRENT_PACK_ID = "CURRENT_PACK_ID";
    public static final String KEY_GROUP_ID = "GROUP_ID";
    public static final String KEY_GROUP_NAME = "GROUP_NAME";
    public static final String KEY_CHAT_MESSAGE_LENGTH = "CHAT_MESSAGE_LENGTH";
    public static final String KEY_CHAT_PROFILE_ID = "CHAT_PROFILE_ID";
    public static final String KEY_CHAT_NICK_NAME = "CHAT_NICK_NAME";
    public static final String KEY_CHAT_PROFILE_IMAGE_URL = "CHAT_PROFILE_IMAGE_RES_ID";
    public static final String KEY_LATITUDE = "KEY_LATITUDE";
    public static final String KEY_LONGITUDE = "KEY_LONGITUDE";
    public static final String KEY_PLAYER_POSITION = "PLAYER_POSITION";
    public static final String KEY_CONFIG_PROGRAM_ID = "default_program_id";
    public static final String KEY_CONFIG_PACK_ID = "default_pack_id";


    /**
     * Constants for Firebase login
     */
    public static final String PASSWORD_PROVIDER = "password";
    public static final String GOOGLE_PROVIDER = "google.com";
    public static final String PROVIDER_DATA_DISPLAY_NAME = "displayName";


    /**
     * Suffix for firebase storage files
     */
    public static final String SUFFIX_AUDIO = "_audio";
    public static final String SUFFIX_VIDEO = "_video";
    public static final String SUFFIX_ICON_IMAGE = "_iconImage";
    public static final String SUFFIX_BANNER_IMAGE = "_bannerImage";

    public static final String DEFUALT_PROGRAM_COLOR = "#616161";

    /**
     * parameters for chat messages
     */
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 180;

    /**
     * GeoFire
     */
    public static final GeoLocation INITIAL_CENTER = new GeoLocation(12.9743926, 77.5935423);

    /**
     * Validation
     */
    public static final int URL_TIMEOUT = 5000;

    /**
     * Bad Words for moderation
     */
    public static final List<String> badWords = Arrays.asList("4r5e", "5h1t", "5hit", "a55", "anal", "anus", "ar5e", "arrse", "arse", "ass", "ass-fucker", "asses", "assfucker", "assfukka", "asshole", "assholes", "asswhole", "a_s_s", "b!tch", "b00bs", "b17ch", "b1tch", "ballbag", "balls", "ballsack", "bastard", "beastial", "beastiality", "bellend", "bestial", "bestiality", "bi+ch", "biatch", "bitch", "bitcher", "bitchers", "bitches", "bitchin", "bitching", "bloody", "blow job", "blowjob", "blowjobs", "boiolas", "bollock", "bollok", "boner", "boob", "boobs", "booobs", "boooobs", "booooobs", "booooooobs", "breasts", "buceta", "bugger", "bum", "bunny fucker", "butt", "butthole", "buttmuch", "buttplug", "c0ck", "c0cksucker", "carpet muncher", "cawk", "chink", "cipa", "cl1t", "clit", "clitoris", "clits", "cnut", "cock", "cock-sucker", "cockface", "cockhead", "cockmunch", "cockmuncher", "cocks", "cocksuck", "cocksucked", "cocksucker", "cocksucking", "cocksucks", "cocksuka", "cocksukka", "cok", "cokmuncher", "coksucka", "coon", "cox", "crap", "cum", "cummer", "cumming", "cums", "cumshot", "cunilingus", "cunillingus", "cunnilingus", "cunt", "cuntlick", "cuntlicker", "cuntlicking", "cunts", "cyalis", "cyberfuc", "cyberfuck", "cyberfucked", "cyberfucker", "cyberfuckers", "cyberfucking", "d1ck", "damn", "dick", "dickhead", "dildo", "dildos", "dink", "dinks", "dirsa", "dlck", "dog-fucker", "doggin", "dogging", "donkeyribber", "doosh", "duche", "dyke", "ejaculate", "ejaculated", "ejaculates", "ejaculating", "ejaculatings", "ejaculation", "ejakulate", "f u c k", "f u c k e r", "f4nny", "fag", "fagging", "faggitt", "faggot", "faggs", "fagot", "fagots", "fags", "fanny", "fannyflaps", "fannyfucker", "fanyy", "fatass", "fcuk", "fcuker", "fcuking", "feck", "fecker", "felching", "fellate", "fellatio", "fingerfuck", "fingerfucked", "fingerfucker", "fingerfuckers", "fingerfucking", "fingerfucks", "fistfuck", "fistfucked", "fistfucker", "fistfuckers", "fistfucking", "fistfuckings", "fistfucks", "flange", "fook", "fooker", "fuck", "fucka", "fucked", "fucker", "fuckers", "fuckhead", "fuckheads", "fuckin", "fucking", "fuckings", "fuckingshitmotherfucker", "fuckme", "fucks", "fuckwhit", "fuckwit", "fudge packer", "fudgepacker", "fuk", "fuker", "fukker", "fukkin", "fuks", "fukwhit", "fukwit", "fux", "fux0r", "f_u_c_k", "gangbang", "gangbanged", "gangbangs", "gaylord", "gaysex", "goatse", "God", "god-dam", "god-damned", "goddamn", "goddamned", "hardcoresex", "hell", "heshe", "hoar", "hoare", "hoer", "homo", "hore", "horniest", "horny", "hotsex", "jack-off", "jackoff", "jap", "jerk-off", "jism", "jiz", "jizm", "jizz", "kawk", "knob", "knobead", "knobed", "knobend", "knobhead", "knobjocky", "knobjokey", "kock", "kondum", "kondums", "kum", "kummer", "kumming", "kums", "kunilingus", "l3i+ch", "l3itch", "labia", "lust", "lusting", "m0f0", "m0fo", "m45terbate", "ma5terb8", "ma5terbate", "masochist", "master-bate", "masterb8", "masterbat*", "masterbat3", "masterbate", "masterbation", "masterbations", "masturbate", "mo-fo", "mof0", "mofo", "mothafuck", "mothafucka", "mothafuckas", "mothafuckaz", "mothafucked", "mothafucker", "mothafuckers", "mothafuckin", "mothafucking", "mothafuckings", "mothafucks", "mother fucker", "motherfuck", "motherfucked", "motherfucker", "motherfuckers", "motherfuckin", "motherfucking", "motherfuckings", "motherfuckka", "motherfucks", "muff", "mutha", "muthafecker", "muthafuckker", "muther", "mutherfucker", "n1gga", "n1gger", "nazi", "nigg3r", "nigg4h", "nigga", "niggah", "niggas", "niggaz", "nigger", "niggers", "nob", "nob jokey", "nobhead", "nobjocky", "nobjokey", "numbnuts", "nutsack", "orgasim", "orgasims", "orgasm", "orgasms", "p0rn", "pawn", "pecker", "penis", "penisfucker", "phonesex", "phuck", "phuk", "phuked", "phuking", "phukked", "phukking", "phuks", "phuq", "pigfucker", "pimpis", "piss", "pissed", "pisser", "pissers", "pisses", "pissflaps", "pissin", "pissing", "pissoff", "poop", "porn", "porno", "pornography", "pornos", "prick", "pricks", "pron", "pube", "pusse", "pussi", "pussies", "pussy", "pussys", "rectum", "retard", "rimjaw", "rimming", "s hit", "s.o.b.", "sadist", "schlong", "screwing", "scroat", "scrote", "scrotum", "semen", "sex", "sh!+", "sh!t", "sh1t", "shag", "shagger", "shaggin", "shagging", "shemale", "shi+", "shit", "shitdick", "shite", "shited", "shitey", "shitfuck", "shitfull", "shithead", "shiting", "shitings", "shits", "shitted", "shitter", "shitters", "shitting", "shittings", "shitty", "skank", "slut", "sluts", "smegma", "smut", "snatch", "son-of-a-bitch", "spac", "spunk", "s_h_i_t", "t1tt1e5", "t1tties", "teets", "teez", "testical", "testicle", "tit", "titfuck", "tits", "titt", "tittie5", "tittiefucker", "titties", "tittyfuck", "tittywank", "titwank", "tosser", "turd", "tw4t", "twat", "twathead", "twatty", "twunt", "twunter", "v14gra", "v1gra", "vagina", "viagra", "vulva", "w00se", "wang", "wank", "wanker", "wanky", "whoar", "whore", "willies", "willy", "xrated", "xxx");

}
