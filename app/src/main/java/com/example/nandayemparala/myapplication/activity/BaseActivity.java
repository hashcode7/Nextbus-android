package com.example.nandayemparala.myapplication.activity;

import android.support.v7.app.AppCompatActivity;

import com.example.nandayemparala.myapplication.model.ormlite.DatabaseHelper;

/**
 * Created by Nanda Yemparala on 9/9/16.
 */
public class BaseActivity extends AppCompatActivity {


    // TODO Empty class.
    protected DatabaseHelper getHelper(){
        return DatabaseHelper.getInstance(this);
    }


//    private volatile DatabaseHelper helper;
//    private volatile boolean created = false;
//    private volatile boolean destroyed = false;
//    private static Logger logger = LoggerFactory.getLogger(OrmLiteBaseActivity.class);
//
//    /**
//     * Get a helper for this action.
//     */
//    public DatabaseHelper getHelper() {
//        if (helper == null) {
//            if (!created) {
//                throw new IllegalStateException("A call has not been made to onCreate() yet so the helper is null");
//            } else if (destroyed) {
//                throw new IllegalStateException(
//                        "A call to onDestroy has already been made and the helper cannot be used after that point");
//            } else {
//                throw new IllegalStateException("Helper is null for some unknown reason");
//            }
//        } else {
//            return helper;
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        if (helper == null) {
//            helper = getHelperInternal(this);
//            created = true;
//        }
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        releaseHelper(helper);
//        destroyed = true;
//    }
//
//    protected DatabaseHelper getHelperInternal(Context context) {
////        @SuppressWarnings({ "unchecked", "deprecation" })
//        DatabaseHelper newHelper = (DatabaseHelper) OpenHelperManager.getHelper(context);
//        logger.trace("{}: got new helper {} from OpenHelperManager", this, newHelper);
//        return newHelper;
//    }
//
//    protected void releaseHelper(DatabaseHelper helper) {
//        OpenHelperManager.releaseHelper();
//        logger.trace("{}: helper {} was released, set to null", this, helper);
//        this.helper = null;
//    }

}
