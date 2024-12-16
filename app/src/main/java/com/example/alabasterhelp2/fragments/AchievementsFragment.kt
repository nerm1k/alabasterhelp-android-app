import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alabasterhelp2.R

class AchievementsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var achievements: List<Achievement>
    private lateinit var recyclerView: RecyclerView
    private lateinit var achievementsAdapter: AchievementsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_achievements, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        // Получаем количество дней захода
        val daysCount = sharedPreferences.getInt("days_count", 0)
        val achievementsCount = sharedPreferences.getInt("achievements_count", 0)

        // Устанавливаем текст
        view.findViewById<TextView>(R.id.text_days).text = "$daysCount"
        view.findViewById<TextView>(R.id.text_achievements).text = "$achievementsCount"

        // Инициализация RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_achievements)

        val layoutManager = GridLayoutManager(context, 3)
        recyclerView.layoutManager = layoutManager

        achievements = loadAchievements()
        achievementsAdapter = AchievementsAdapter(achievements)
        recyclerView.adapter = achievementsAdapter
        // recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun loadAchievements(): List<Achievement> {
        // Здесь вы можете загрузить достижения из SharedPreferences или создать их вручную
        return listOf(
            Achievement("Новый пользователь", true),
            Achievement("Новичок", false),
            Achievement("Первый раз", true),
            Achievement("Перфекционист недели", false),
            Achievement("Опытный", false),
            Achievement("Фотоотчет", false),
            Achievement("Перфекционист месяца", false),
            Achievement("Старик", false),
            Achievement("Любитель статистики", false)
        )
    }
}

class AchievementsAdapter(private val achievements: List<Achievement>) : RecyclerView.Adapter<AchievementsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.achievement_name)
        val medalImageView: ImageView = view.findViewById(R.id.medal_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_achievement, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val achievement = achievements[position]
        holder.nameTextView.text = achievement.name
        holder.medalImageView.setImageResource(if (achievement.isCompleted) R.mipmap.ic_medalcomplete else R.mipmap.medalnotcomplete)
    }

    override fun getItemCount(): Int {
        return achievements.size
    }
}

data class Achievement(val name: String, val isCompleted: Boolean)