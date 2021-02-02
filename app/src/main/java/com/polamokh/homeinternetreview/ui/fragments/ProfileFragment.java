package com.polamokh.homeinternetreview.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.polamokh.homeinternetreview.R;
import com.polamokh.homeinternetreview.data.Review;
import com.polamokh.homeinternetreview.data.User;
import com.polamokh.homeinternetreview.data.dao.CompanyDao;
import com.polamokh.homeinternetreview.data.dao.ReviewDao;
import com.polamokh.homeinternetreview.data.dao.UserDao;
import com.polamokh.homeinternetreview.ui.adapters.ReviewsAdapter;
import com.polamokh.homeinternetreview.ui.listeners.IOnItemSelectListener;
import com.polamokh.homeinternetreview.utils.BitmapUtils;
import com.polamokh.homeinternetreview.utils.FirebaseAuthUtils;
import com.polamokh.homeinternetreview.utils.GoogleSignInUtils;
import com.polamokh.homeinternetreview.utils.ImagePickerUtils;
import com.polamokh.homeinternetreview.viewmodel.ProfileViewModel;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfileFragment extends Fragment implements IOnItemSelectListener {
    private static final String TAG = ProfileFragment.class.getSimpleName();

    private ProfileViewModel profileViewModel;

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private NestedScrollView scrollView;
    private ProgressBar progressBar;
    private ImageView profilePic;
    private TextView name;
    private TextView email;
    private Button removeAccount;
    private Button signOut;
    private Button signIn;
    private ImageButton editProfilePic;
    private RecyclerView myReviewsRecyclerView;
    private ReviewsAdapter adapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (firebaseAuth.getCurrentUser() != null)
            return inflater.inflate(R.layout.fragment_profile, container, false);
        else
            return inflater.inflate(R.layout.fragment_profile_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if ((firebaseAuth.getCurrentUser() != null)) {
            initializeUserProfile(view);
        } else {
            initializeSignIn(view);
        }
    }

    private void initializeSignIn(View view) {
        signIn = view.findViewById(R.id.profile_sign_in);
        signIn.setOnClickListener(v -> FirebaseAuthUtils.startAuthUserActivity(this,
                FirebaseAuthUtils.RC_SIGN_IN));
    }

    private void initializeUserProfile(View view) {
        profileViewModel = new ViewModelProvider(ViewModelStore::new)
                .get(ProfileViewModel.class);

        scrollView = view.findViewById(R.id.profile_scroll);
        progressBar = view.findViewById(R.id.progress);
        profilePic = view.findViewById(R.id.profile_pic);
        name = view.findViewById(R.id.profile_name_text);
        email = view.findViewById(R.id.profile_email_text);
        removeAccount = view.findViewById(R.id.profile_remove_account);
        editProfilePic = view.findViewById(R.id.profile_pic_edit);
        signOut = view.findViewById(R.id.profile_sign_out);
        myReviewsRecyclerView = view.findViewById(R.id.my_reviews_recycler_view);

        profileViewModel.setUser(firebaseAuth.getCurrentUser());

        profileViewModel.isLoading().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                showLoading();
            else
                hideLoading();
        });

        profileViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            Toast.makeText(requireContext(),
                    user.getUid(),
                    Toast.LENGTH_SHORT)
                    .show();

            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.ic_profile_24dp)
                    .circleCrop()
                    .into(profilePic);

            name.setText(user.getDisplayName());
            email.setText(user.getEmail());
            profileViewModel.getReviews();
        });

        editProfilePic.setOnClickListener(v -> ImagePickerUtils.startPickImageActivity(this));
        name.setOnClickListener(v -> showEditProfileNameDialog());

        signOut.setOnClickListener(v -> {
            firebaseAuth.signOut();
            reloadFragment();
        });

        removeAccount.setOnClickListener(v -> {
            FirebaseUser user = profileViewModel.getUser().getValue();
            reauthorizeAndDeleteUser(user);
        });

        initializeMyReviewsRecyclerView();
        profileViewModel.getReviews().observe(getViewLifecycleOwner(), reviews ->
                adapter.setReviews(reviews));
    }

    private void initializeMyReviewsRecyclerView() {
        adapter = new ReviewsAdapter(Glide.with(this), this);
        myReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        myReviewsRecyclerView.setAdapter(adapter);
    }

    private void reauthorizeAndDeleteUser(FirebaseUser user) {
        switch (user.getProviderData().get(1).getProviderId()) {
            case EmailAuthProvider.PROVIDER_ID:
                showAuthorizeProfileDialog();
                return;

            case GoogleAuthProvider.PROVIDER_ID:
                GoogleSignInUtils.startSignInActivity(this, GoogleSignInUtils.RC_SIGN_IN);
                return;

            default:
        }
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        scrollView.scrollTo(0, 0);
    }

    private void reloadFragment() {
        if (isVisible())
            getParentFragmentManager().beginTransaction()
                    .detach(this).attach(this).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case FirebaseAuthUtils.RC_SIGN_IN:
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    IdpResponse response = IdpResponse.fromResultIntent(data);

                    if (response.isNewUser())
                        UserDao.getInstance().create(new User(user));

                    reloadFragment();
                    return;

                case GoogleSignInUtils.RC_SIGN_IN:
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    AuthCredential credential =
                                            getGoogleCredential(task.getResult().getIdToken());
                                    reauthorizeProfile(credential);
                                }
                            });
                    return;

                case ImagePickerUtils.RC_PICK_IMAGE:
                    ImagePickerUtils.startCropImageActivity(data.getData(), this);
                    return;

                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    handleCropImageResult(CropImage.getActivityResult(data).getUri());
                    return;

                default:
            }
        }
    }

    private void handleCropImageResult(Uri imageUri) {
        InputStream inputStream;
        try {
            inputStream =
                    requireContext().getContentResolver().openInputStream(imageUri);

            Bitmap bitmap =
                    BitmapUtils.convertInputStreamToBitmap(inputStream, imageUri.toString());

            profileViewModel.uploadProfilePicture(BitmapUtils.convertBitmapToInputStream(bitmap));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showEditProfileNameDialog() {
        EditProfileNameDialogFragment dialogFragment =
                new EditProfileNameDialogFragment(profileViewModel.getUser().getValue().getDisplayName(),
                        extra -> profileViewModel.setProfileName(extra.toString()));
        dialogFragment.show(getParentFragmentManager(), null);
    }

    private void showDeleteProfileDialog() {
        DeleteProfileDialogFragment dialogFragment =
                new DeleteProfileDialogFragment(extra -> {
                    profileViewModel.deleteProfilePicture();
                    profileViewModel.deleteReviews();
                    profileViewModel.deleteProfileInfo();
                    profileViewModel.deleteProfile()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful())
                                    reloadFragment();
                                else
                                    Log.d(TAG, "onActivityResult: DELETE PROFILE FAILED");
                            });
                });
        dialogFragment.show(getParentFragmentManager(), null);
    }

    private void showAuthorizeProfileDialog() {
        AuthorizeProfileDialogFragment dialogFragment =
                new AuthorizeProfileDialogFragment(extra -> {
                    AuthCredential credential =
                            getEmailCredential(profileViewModel.getUser().getValue().getEmail(),
                                    extra.toString());
                    reauthorizeProfile(credential);
                });
        dialogFragment.show(getParentFragmentManager(), null);
    }

    private void reauthorizeProfile(AuthCredential credential) {
        profileViewModel.reauthorizeProfile(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        showDeleteProfileDialog();
                    else
                        Toast.makeText(requireContext(),
                                task.getException().getMessage(),
                                Toast.LENGTH_SHORT)
                                .show();
                });
    }

    private AuthCredential getEmailCredential(String email, String password) {
        return EmailAuthProvider.getCredential(email, password);
    }

    private AuthCredential getGoogleCredential(String idToken) {
        return GoogleAuthProvider.getCredential(idToken, null);
    }

    @Override
    public void OnItemSelected(Object object) {
        Review review = (Review) object;
        ReviewDao.getInstance().delete(review.getId())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        CompanyDao.getInstance()
                                .updateCompaniesStandings(review, CompanyDao.updateState.REMOVED);
                        Snackbar.make(requireView(),
                                R.string.profile_delete_review_message,
                                Snackbar.LENGTH_LONG)
                                .setAnchorView(requireActivity().findViewById(R.id.bottom_navigation_view))
                                .setAction(R.string.profile_delete_review_action, v -> {
                                    ReviewDao.getInstance().getAll()
                                            .child(review.getId())
                                            .setValue(review)
                                            .addOnCompleteListener(restoreTask -> {
                                                if (restoreTask.isSuccessful())
                                                    CompanyDao.getInstance()
                                                            .updateCompaniesStandings(review,
                                                                    CompanyDao.updateState.INSERTED);
                                            });
                                })
                                .show();
                    }
                });
    }
}
