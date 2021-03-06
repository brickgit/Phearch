package com.brickgit.imagesearch.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.brickgit.imagesearch.R;
import com.brickgit.imagesearch.adapter.ImageAdapter;
import com.brickgit.imagesearch.adapter.SpaceItemDecoration;
import com.brickgit.imagesearch.model.MediaViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageFragment extends Fragment {

	private MediaViewModel mViewModel;

	@BindView(R.id.image_progress_bar) ProgressBar progressBar;
	@BindView(R.id.image_list_view) RecyclerView listView;

	private StaggeredGridLayoutManager layoutManager;

	private final ImageAdapter.OnImageCellClickListener onImageCellClickListener = imageInfo -> {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageInfo.pageURL));
		startActivity(intent);
	};

	private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);

			int totalItemCount = layoutManager.getItemCount();
			int visibleItemCount = layoutManager.getChildCount();
			int[] firstVisibleItemPositions = layoutManager.findFirstVisibleItemPositions(null);
			int firstVisibleItemPosition = firstVisibleItemPositions[firstVisibleItemPositions.length - 1];

			if (firstVisibleItemPosition + visibleItemCount >= totalItemCount) {
				mViewModel.loadMorePhotos();
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_image, container, false);
		ButterKnife.bind(this, view);

		layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
		listView.setLayoutManager(layoutManager);
		ImageAdapter adapter = new ImageAdapter();
		adapter.setOnImageCellClickListener(onImageCellClickListener);
		listView.setAdapter(adapter);
		listView.addItemDecoration(new SpaceItemDecoration());
		listView.addOnScrollListener(onScrollListener);

		mViewModel = ViewModelProviders.of(getActivity()).get(MediaViewModel.class);
		mViewModel.getQuery().observe(this, query -> {
			showProgressBar(true);
			adapter.clear();
		});
		mViewModel.getPhotos().observe(this, imageInfoResponses -> {
			showProgressBar(false);
			adapter.update(imageInfoResponses);
		});

		return view;
	}

	private void showProgressBar(boolean show) {
		progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
	}
}
