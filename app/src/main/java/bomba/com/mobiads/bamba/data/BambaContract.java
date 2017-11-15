package bomba.com.mobiads.bamba.data;

import android.provider.BaseColumns;

/**
 * Created by WAKY on 2/19/2017.
 */
public class BambaContract {
    public static final class AccountEntry implements BaseColumns {

        public static final String TABLE_NAME = "bamba_accounts";

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_FPATH ="file_path";
        public static final String COLUMN_SAVED ="ac_saved";
        public static final String COLUMN_CREATED_AT ="created_at";

        public static final String[] PROJECTION = {
                "_ID AS _id",
                COLUMN_NAME,
                COLUMN_PHONE,
                COLUMN_SAVED,
                COLUMN_CREATED_AT
        };
    }

    public static final class TonesEntry implements BaseColumns {

        public static final String TABLE_NAME = "bamba_tones";

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PATH ="file_path";
        public static final String COLUMN_STATUS ="status";
        public static final String COLUMN_CREATED_AT ="created_at";

        public static final String[] PROJECTION = {
                "_ID AS _id",
                COLUMN_PHONE,
                COLUMN_NAME,
                COLUMN_PATH,
                COLUMN_STATUS,
                COLUMN_CREATED_AT
        };
    }
}
