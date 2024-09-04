package com.example.transactionsms;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categoryList;

    public CategoryAdapter(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView categoryLogoImageView;
        private TextView categoryCountTextView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryLogoImageView = itemView.findViewById(R.id.categoryLogoImageView);
            categoryCountTextView = itemView.findViewById(R.id.categoryCountTextView);
        }

        public void bind(Category category) {
            categoryLogoImageView.setImageResource(category.getLogoResId());
            categoryCountTextView.setText(String.valueOf(category.getCount()));
        }
    }
}