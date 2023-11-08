package jonathanespinal.com.androidfinalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProjectImageAdapter (private var imageList: List<ProjectImage>) :
    RecyclerView.Adapter<ProjectImageAdapter.ProjectImageViewHolder>(){
    private lateinit var listener : ProjectImageAdapterListener

    inner class ProjectImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageViewProjectImage : ImageView = itemView.findViewById(R.id.imageViewProjectImage)

        init {
            itemView.setOnClickListener {
                if (listener != null) {
                    var position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onClick(position)
                    }
                }
            }
        }
    }

    interface ProjectImageAdapterListener {
        fun onClick(position: Int)
    }

    fun setOnItemClickListener(_listener : ProjectImageAdapterListener) {
        listener = _listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.galleryitemlayout, parent, false)
        return ProjectImageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    fun setData(list: List<ProjectImage>) {
        imageList = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ProjectImageViewHolder, position: Int) {
        val image = imageList[position]

        // Reference to an image file in Cloud Storage
        //val storageReference = Firebase.storage.reference

        // Download directly from StorageReference using Glide
        // (See MyAppGlideModule for Loader registration)
        Glide.with(holder.imageViewProjectImage)
            .load(image.imageURL)
          //  .override(800,800)
            .centerInside()
            .into(holder.imageViewProjectImage)

        //holder.imageViewModelPreview.setImageURI(Uri.parse(model.imageURL))
    }
}