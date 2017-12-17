package jayjaybinks.github.com.concertsforyou;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;

import static jayjaybinks.github.com.concertsforyou.Concert.*;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConcertFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConcertFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConcertFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Concert concert;

    public ConcertFragment() {
        // Required empty public constructor
    }

    public Bitmap loadImageBitmap(String imageName) {
        Bitmap bitmap = null;
        FileInputStream fiStream;
        try {
            fiStream    = getActivity().getApplicationContext().openFileInput(imageName);
            bitmap      = BitmapFactory.decodeStream(fiStream);
            fiStream.close();
        } catch (Exception e) {
            Log.d("saveImage", "Exception 3, Something went wrong!");
            e.printStackTrace();
        }
        return bitmap;
    }

    public static ConcertFragment newInstance(Concert concert) {
        ConcertFragment fragment = new ConcertFragment();
        fragment.setArguments(concert.getAsBundle());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            concert = new Concert(arguments.getString(ARTIST_NAME),
                    arguments.getString(CONCERT_DATE),
                    arguments.getString(IMAGE_NAME),
                    arguments.getString(VENUE_NAME),
                    arguments.getString(VENUE_COUNTRY),
                    arguments.getString(VENUE_CITY));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_artist_concert, container, false);
        TextView tvArtistName = view.findViewById(R.id.artist_name);
        tvArtistName.setText(concert.getArtistName());

        TextView tvConcertDate = view.findViewById(R.id.concert_date);
        tvConcertDate.setText(concert.getConcertDate());

        TextView tvConcertVenue = view.findViewById(R.id.venue_name);
        tvConcertVenue.setText(concert.getVenueName());

        ImageView tvArtistImage = view.findViewById(R.id.artist_image);
        tvArtistImage.setImageBitmap(loadImageBitmap(concert.getArtistName() + ".png"));

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
