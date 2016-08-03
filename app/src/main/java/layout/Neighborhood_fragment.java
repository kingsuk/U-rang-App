package layout;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import us.tier5.u_rang.AsyncResponse;
import us.tier5.u_rang.R;
import us.tier5.u_rang.RegisterUser;
import us.tier5.u_rang.UserConstants;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Neighborhood_fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Neighborhood_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Neighborhood_fragment extends Fragment implements AsyncResponse.Response{
    // Own custom variables
    RegisterUser registerUser = new RegisterUser("POST");
    HashMap<String, String> data = new HashMap<String,String>();
    ArrayList<AsyncTask> asyncTasksArr = new ArrayList<>();
    String route = "/V1/get-neighborhoods";
    LinearLayout lm;
    ProgressDialog loading;
    Bundle mySaveInstanceState;
    AsyncTask asyncTask;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Neighborhood_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Neighborhood_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Neighborhood_fragment newInstance(String param1, String param2) {
        Neighborhood_fragment fragment = new Neighborhood_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //setting view elements
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("NEIGHBORHOODS");
        mySaveInstanceState = savedInstanceState;
        View fragView = inflater.inflate(R.layout.content_faq, container, false);

        lm = (LinearLayout) fragView.findViewById(R.id.llParent);
        registerUser.delegate = this;
        registerUser.register(data,route);
        loading = ProgressDialog.show(getContext(), "Please Wait",null, true, true);

        return fragView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction("uri");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        for (int i=0; i<asyncTasksArr.size();i++)
        {
            asyncTasksArr.get(i).cancel(true);
            //Log.i("kingsukmajumder","stopped "+i+" async task");
        }
        Log.i("kingsukmajumder","OnDestroy");
        super.onDestroy();
    }

    @Override
    public void onPause() {
        Log.i("kingsukmajumder","onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.i("kingsukmajumder","onResume");
        super.onResume();
    }
    int threadCount = 0;
    @Override
    public void processFinish(String output) {
        loading.dismiss();
        Log.i("kingsukmajumder",output);

        try{
            JSONObject jsonObject = new JSONObject(output);
            if(jsonObject.getBoolean("status"))
            {
                String response = jsonObject.getString("response");
                JSONArray jsonArray = new JSONArray(response);
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject currentObj = jsonArray.getJSONObject(i);
                    String name = currentObj.getString("name");
                    String description = Html.fromHtml(currentObj.getString("description")).toString();
                    String imageName = currentObj.getString("image");
                    final String imageUrl = UserConstants.BASE_URL+UserConstants.IMAGE_FOLDER+imageName;

                    View inflatedLayout= getLayoutInflater(mySaveInstanceState).inflate(R.layout.neighborhood, null, false);
                    TextView title = (TextView) inflatedLayout.findViewById(R.id.tvNeighborhoodTitle);
                    title.setText(name);
                    TextView text = (TextView) inflatedLayout.findViewById(R.id.tvNeighborhoodText);
                    text.setText(description);
                    final TextView loadingTV = (TextView) inflatedLayout.findViewById(R.id.tvNeighborLoading);
                    /*View linlaHeaderProgress = (View) findViewById(R.id.progressBarNeighbor);
                    linlaHeaderProgress.setVisibility(View.VISIBLE);*/
                    final ImageView imageViewNeigbor = (ImageView) inflatedLayout.findViewById(R.id.ivNeighborhoodImage);

                    try{
                         asyncTask = new AsyncTask<Void, Void, Void>() {

                            Bitmap bmp;
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                            }
                            @Override
                            protected Void doInBackground(Void... params) {

                                try {
                                    InputStream in = new URL(imageUrl).openStream();
                                    bmp = BitmapFactory.decodeStream(in);
                                } catch (Exception e) {
                                    Toast.makeText(getContext(),"Some error occoured while loading images!",Toast.LENGTH_LONG).show();
                                    Log.i("kingsukmajumder","error in loading images "+e.toString());
                                }


                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void result) {
                                //loading.dismiss();
                                if (bmp != null)
                                    loadingTV.setVisibility(View.INVISIBLE);
                                imageViewNeigbor.setImageBitmap(bmp);
                                Log.i("kingsukmajumder","thread "+threadCount+" is finished");
                                threadCount++;
                            }
                        }.execute();

                        asyncTasksArr.add(asyncTask);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getContext(),"Cannot show all images",Toast.LENGTH_SHORT).show();
                        Log.i("kingsukmajumder",e.toString());
                    }




                    lm.addView(inflatedLayout);
                }
            }
            else
            {
                Toast.makeText(getContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(),"Error in fetching Neighborhood",Toast.LENGTH_SHORT).show();
            Log.i("kingsukmajumder",e.toString());
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String swag);
    }
}
