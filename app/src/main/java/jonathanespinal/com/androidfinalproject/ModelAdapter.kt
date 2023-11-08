package jonathanespinal.com.androidfinalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ModelAdapter (private var modelList: List<Model>) :
    RecyclerView.Adapter<ModelAdapter.ModelViewHolder>(){
    private lateinit var listener : ModelAdapterListener

    inner class ModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewModelName : TextView = itemView.findViewById(R.id.textViewModelName)
        val textViewColorScheme : TextView = itemView.findViewById(R.id.textViewColorScheme)
        var imageViewModelPreview : ImageView = itemView.findViewById(R.id.imageViewModelPreview)

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

    interface ModelAdapterListener {
        fun onClick(position: Int)
    }

    fun setOnItemClickListener(_listener : ModelAdapterListener) {
        listener = _listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemlayout, parent, false)
        return ModelViewHolder(view)
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    fun setData(list: List<Model>) {
        modelList = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        val model = modelList[position]
        holder.textViewModelName.text = model.name
        holder.textViewColorScheme.text = model.description

        // Reference to an image file in Cloud Storage
        //val storageReference = Firebase.storage.reference

        // Download directly from StorageReference using Glide
        // (See MyAppGlideModule for Loader registration)
        Glide.with(holder.imageViewModelPreview)
            .load(model.imageURL)
            .override(500, 500)
            .centerInside()
            .into(holder.imageViewModelPreview)

        //holder.imageViewModelPreview.setImageURI(Uri.parse(model.imageURL))
    }
}