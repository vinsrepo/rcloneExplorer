package ca.pkay.rcloneexplorer.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ca.pkay.rcloneexplorer.Items.FileItem;
import ca.pkay.rcloneexplorer.R;
import ca.pkay.rcloneexplorer.Rclone;
import ca.pkay.rcloneexplorer.RecyclerViewAdapters.FileExplorerRecyclerViewAdapter;

public class FileExplorerFragment extends Fragment {

    private static final String ARG_REMOTE = "remote_param";
    private static final String ARG_PATH = "path_param";
    private Rclone rclone;
    private String remote;
    private String path;
    private FileExplorerRecyclerViewAdapter recyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FileExplorerFragment() {
    }

    @SuppressWarnings("unused")
    public static FileExplorerFragment newInstance(String remote, String path) {
        FileExplorerFragment fragment = new FileExplorerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REMOTE, remote);
        args.putString(ARG_PATH, path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            remote = getArguments().getString(ARG_REMOTE);
            path = getArguments().getString(ARG_PATH);
        }
        getActivity().setTitle(remote);
        rclone = new Rclone((AppCompatActivity) getActivity());

        new FetchDirectoryContent().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_explorer_list, container, false);

        // set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            recyclerViewAdapter = new FileExplorerRecyclerViewAdapter(null);
            recyclerView.setAdapter(recyclerViewAdapter);
        }
        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchDirectoryContent extends AsyncTask<Void, Void, List<FileItem>> {

        @Override
        protected List<FileItem> doInBackground(Void... voids) {
            List<FileItem> fileItemList;
            fileItemList = rclone.getDirectoryContent(remote, path);
            return fileItemList;
        }

        @Override
        protected void onPostExecute(List<FileItem> fileItems) {
            super.onPostExecute(fileItems);
            recyclerViewAdapter.newData(fileItems);
        }
    }
}