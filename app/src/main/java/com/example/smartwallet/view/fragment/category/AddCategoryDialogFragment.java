package com.example.smartwallet.view.fragment.category;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smartwallet.R;
import com.example.smartwallet.databinding.FragmentAddCategoryDialogBinding;
import com.example.smartwallet.model.Category;
import com.example.smartwallet.utils.SessionManager;
import com.example.smartwallet.viewmodel.CategoryViewModel;

public class AddCategoryDialogFragment extends DialogFragment {
    private EditText inputCategoryName;
    private CategoryViewModel categoryViewModel;
    private SessionManager sessionManager;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_add_category_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext()).setView(view).create();

        FragmentAddCategoryDialogBinding fragmentAddCategoryDialogBinding = FragmentAddCategoryDialogBinding.bind(view);

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        sessionManager = new SessionManager(requireContext());

        inputCategoryName = fragmentAddCategoryDialogBinding.editTextInputDetail;
        Button buttonAdd = fragmentAddCategoryDialogBinding.buttonEdit;
        Button buttonCancel = fragmentAddCategoryDialogBinding.buttonDelete;

        buttonAdd.setOnClickListener(v -> {
            if (checkInput()) {
                Category category = new Category(inputCategoryName.getText().toString().trim(), sessionManager.getUsername());
                categoryViewModel.addCategory(category).observe(this, result -> {
                    if (result) {
                        Toast.makeText(requireContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                        clearInput();
                        dismiss();
                    } else {
                        Toast.makeText(requireContext(), "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        buttonCancel.setOnClickListener(v -> dismiss());

        return dialog;
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
}
