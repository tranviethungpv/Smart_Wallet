package com.example.smartwallet.view.fragment.category;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartwallet.R;
import com.example.smartwallet.databinding.FragmentUpdateCategoryDialogBinding;
import com.example.smartwallet.listener.OnCategoryChangedListener;
import com.example.smartwallet.model.Category;
import com.example.smartwallet.utils.SessionManager;
import com.example.smartwallet.viewmodel.CategoryViewModel;

public class UpdateCategoryDialogFragment extends DialogFragment {
    private OnCategoryChangedListener onCategoryChangedListener;
    private Category selectedCategory;
    private EditText inputCategoryName;
    private CategoryViewModel categoryViewModel;
    private SessionManager sessionManager;
    private FragmentUpdateCategoryDialogBinding fragmentUpdateCategoryDialogBinding;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_add_category_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext()).setView(view).create();

        fragmentUpdateCategoryDialogBinding = FragmentUpdateCategoryDialogBinding.bind(view);

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        sessionManager = new SessionManager(requireContext());

        inputCategoryName = fragmentUpdateCategoryDialogBinding.editTextInputDetail;
        Button buttonAdd = fragmentUpdateCategoryDialogBinding.buttonEdit;
        Button buttonCancel = fragmentUpdateCategoryDialogBinding.buttonDelete;

        getData();
        fillData();

        buttonAdd.setOnClickListener(v -> {
            if (checkInput()) {
                Category category = new Category(selectedCategory.getId(), inputCategoryName.getText().toString().trim(), sessionManager.getUsername());
                categoryViewModel.updateCategory(category).observe(this, result -> {
                    if (result) {
                        Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        clearInput();
                        dismiss();
                        if (onCategoryChangedListener != null) {
                            onCategoryChangedListener.onCategoryUpdated();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        buttonCancel.setOnClickListener(v -> {
            dismiss();
            if (onCategoryChangedListener != null) {
                onCategoryChangedListener.onCategoryUpdated();
            }
        });

        return dialog;
    }

    private void getData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String id = bundle.getString("id");
            String userId = bundle.getString("userId");
            String name = bundle.getString("name");
            selectedCategory = new Category(id, name, userId);
        }
    }

    private void fillData() {
        fragmentUpdateCategoryDialogBinding.editTextInputDetail.setText(selectedCategory.getName());
    }

    private Boolean checkInput() {
        String name = inputCategoryName.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            inputCategoryName.setError("Hãy nhập tên danh mục");
            inputCategoryName.requestFocus();
            return false;
        }
        return true;
    }

    private void clearInput() {
        inputCategoryName.setText("");
    }

    public void setOnCategoryChangedListener(OnCategoryChangedListener onCategoryChangedListener) {
        this.onCategoryChangedListener = onCategoryChangedListener;
    }
}