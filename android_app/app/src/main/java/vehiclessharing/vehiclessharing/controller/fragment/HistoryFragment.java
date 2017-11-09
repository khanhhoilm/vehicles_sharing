package vehiclessharing.vehiclessharing.controller.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.vehiclessharing.R;

/**
 * Created by Hihihehe on 6/10/2017.
 */

public class HistoryFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private View view;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static HistoryFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      view=inflater.inflate(R.layout.fragment_history,container,false);
        addControls();
        addEvents();
        return view;
    }

    private void addEvents() {

    }

    private void addControls() {
    }
}
