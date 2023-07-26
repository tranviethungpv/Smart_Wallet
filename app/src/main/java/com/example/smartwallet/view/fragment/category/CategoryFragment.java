package com.example.smartwallet.view.fragment.category;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartwallet.R;
import com.example.smartwallet.adapter.CategoryAdapter;
import com.example.smartwallet.databinding.FragmentCategoryBinding;
import com.example.smartwallet.listener.OnCategoryChangedListener;
import com.example.smartwallet.model.Category;
import com.example.smartwallet.utils.SessionManager;
import com.example.smartwallet.viewmodel.CategoryViewModel;

public class CategoryFragment extends Fragment{
    private FragmentCategoryBinding fragmentCategoryBinding;
    private CategoryAdapter categoryAdapter;
    private CategoryViewModel categoryViewModel;
    private RecyclerView recyclerViewCategory;
    private Category longPressedCategory = new Category();
    private SessionManager sessionManager;

    public CategoryFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sessionManager = new SessionManager(requireContext());
        fragmentCategoryBinding = FragmentCategoryBinding.inflate(inflater, container, false);

        renderListCategory();
        registerForContextMenu(recyclerViewCategory);

        TextView addButton = fragmentCategoryBinding.textView;
        addButton.setOnClickListener(v -> {
            AddCategoryDialogFragment addCategoryDialogFragment = new AddCategoryDialogFragment();
            addCategoryDialogFragment.setOnCategoryChangedListener(new OnCategoryChangedListener() {
                @Override
                public void onCategoryAdded() {
                    renderListCategory();
                }

                @Override
                public void onCategoryUpdated() {
                    renderListCategory();
                }
            });
            addCategoryDialogFragment.show(getChildFragmentManager(), "AddCategoryDialog");
        });
        return fragmentCategoryBinding.getRoot();
    }

    private void renderListCategory() {
        recyclerViewCategory = fragmentCategoryBinding.recyclerCategory;
        recyclerViewCategory.setLayoutManager(new GridLayoutManager(requireActivity(), 1));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        recyclerViewCategory.addItemDecoration(dividerItemDecoration);

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        categoryViewModel.getAllCategories(sessionManager.getUsername()).observe(getViewLifecycleOwner(), categoryArrayList -> {
            if (categoryArrayList.size() == 0) {
                fragmentCategoryBinding.textViewDataStatus.setVisibility(View.VISIBLE);
            } else {
                fragmentCategoryBinding.recyclerCategory.setVisibility(View.VISIBLE);
                categoryAdapter = new CategoryAdapter(categoryArrayList, (category, isLongClick) -> {
                    if (isLongClick) {
                        showContextMenu(category);
                    }
                });
                recyclerViewCategory.setAdapter(categoryAdapter);
            }
        });
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.hold_menu, menu);
        menu.setHeaderTitle(longPressedCategory.getName());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit_item) {
            switchToUpdateCategoryFragment();
        } else if (item.getItemId() == R.id.delete_item) {
            categoryViewModel.deleteCategory(longPressedCategory).observe(getViewLifecycleOwner(), result -> {
                if (result) {
                    Toast.makeText(requireContext(), "Xoá thành công " + longPressedCategory.getName() + "!", Toast.LENGTH_SHORT).show();
                    renderListCategory();
                } else {
                    Toast.makeText(requireContext(), "Xảy ra lỗi khi xoá " + longPressedCategory.getName() + "!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return super.onContextItemSelected(item);
    }

    private void showContextMenu(Category category) {
        longPressedCategory = category;
        recyclerViewCategory.showContextMenu();
    }

    private void switchToUpdateCategoryFragment() {
        UpdateCategoryDialogFragment updateCategoryDialogFragment = new UpdateCategoryDialogFragment();

        Bundle bundle = new Bundle();
        if (longPressedCategory.getId() != null) {
            bundle.putString("id", longPressedCategory.getId());
            bundle.putString("userId", longPressedCategory.getUserId());
            bundle.putString("name", longPressedCategory.getName());
        }
        updateCategoryDialogFragment.setArguments(bundle);
        updateCategoryDialogFragment.setOnCategoryChangedListener(new OnCategoryChangedListener() {
            @Override
            public void onCategoryAdded() {
                renderListCategory();
            }

            @Override
            public void onCategoryUpdated() {
                renderListCategory();
            }
        });

        updateCategoryDialogFragment.show(getChildFragmentManager(), "UpdateCategoryDialog");
    }
}