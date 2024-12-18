import android.content.Context
import android.content.Intent
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
import com.example.alabasterhelp2.AchievementDetailActivity
import com.example.alabasterhelp2.R
import java.util.Calendar
import java.util.Date

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
        // Устанавливаем текст
        view.findViewById<TextView>(R.id.text_days).text = "$daysCount"


        // Инициализация RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_achievements)

        val layoutManager = GridLayoutManager(context, 3)
        recyclerView.layoutManager = layoutManager

        achievements = loadAchievements()
        achievementsAdapter = AchievementsAdapter(achievements)
        recyclerView.adapter = achievementsAdapter
        // recyclerView.layoutManager = LinearLayoutManager(context)

        val achievementsCount = achievementsAdapter.countAchieved()
        // val achievementsCount = sharedPreferences.getInt("achievements_count", 0)
        view.findViewById<TextView>(R.id.text_achievements).text = "$achievementsCount"




        val editor = sharedPreferences.edit()

        // Получаем текущее время
        val currentDate = System.currentTimeMillis()
        // Получаем дату последнего обновления
        val lastUpdate = sharedPreferences.getLong("last_update", 0)

        // Проверяем, был ли день изменён
        val lastUpdateDate = Date(lastUpdate)
        val calendar = Calendar.getInstance()
        calendar.time = lastUpdateDate

        // Сравниваем только даты (без времени)
        if (calendar.get(Calendar.DAY_OF_YEAR) != Calendar.getInstance().get(Calendar.DAY_OF_YEAR) ||
            calendar.get(Calendar.YEAR) != Calendar.getInstance().get(Calendar.YEAR)) {

            // Если день изменился, увеличиваем days_count
            val daysCount = sharedPreferences.getInt("days_count", 0) + 1
            editor.putInt("days_count", daysCount)
            editor.putLong("last_update", currentDate) // Сохраняем текущую дату
        }

        editor.apply()
    }

    private fun loadAchievements(): List<Achievement> {
        // Здесь вы можете загрузить достижения из SharedPreferences или создать их вручную
        return listOf(
            Achievement("Новый пользователь", "Вы успешно зашли в приложение",true),
            Achievement("Новичок", "Вы заходили в приложение 2 дня",false),
            Achievement("Первый раз", "Вы добавили свое первое напоминание", true),
            Achievement("Перфекционист недели", "Необходимо отмечать выполнения напоминаний в течении недели",false),
            Achievement("Опытный", "Вы заходили в приложение 30 дней",false),
            Achievement("Фотоотчет", "Вы добавили свое первое напоминание",false),
            Achievement("Перфекционист месяца", "Необходимо отмечать выполнения напоминаний в течении месяца",false),
            Achievement("Старик", "Вы заходили в приложение 90 дней",false),
            Achievement("Любитель статистики", "Вы успешно поделились своей статистикой",false)
        )
    }

//    override fun onResume() {
//        super.onResume()
//        val editor = sharedPreferences.edit()
//
//        // Получаем текущее время
//        val currentDate = System.currentTimeMillis()
//        // Получаем дату последнего обновления
//        val lastUpdate = sharedPreferences.getLong("last_update", 0)
//
//        // Проверяем, был ли день изменён
//        val lastUpdateDate = Date(lastUpdate)
//        val calendar = Calendar.getInstance()
//        calendar.time = lastUpdateDate
//
//        // Сравниваем только даты (без времени)
//        if (calendar.get(Calendar.DAY_OF_YEAR) != Calendar.getInstance().get(Calendar.DAY_OF_YEAR) ||
//            calendar.get(Calendar.YEAR) != Calendar.getInstance().get(Calendar.YEAR)) {
//
//            // Если день изменился, увеличиваем days_count
//            val daysCount = sharedPreferences.getInt("days_count", 0) + 1
//            editor.putInt("days_count", daysCount)
//            editor.putLong("last_update", currentDate) // Сохраняем текущую дату
//        }
//
//        editor.apply()
//    }

}

class AchievementsAdapter(private val achievements: List<Achievement>) : RecyclerView.Adapter<AchievementsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.achievement_name)
        val medalImageView: ImageView = view.findViewById(R.id.medal_icon)
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val achievement = achievements[position]
                    val context = itemView.context
                    val intent = Intent(context, AchievementDetailActivity::class.java).apply {
                        putExtra("achievement_name", achievement.name)
                        putExtra("achievement_description", achievement.description)
                        putExtra("achievement_is_achieved", achievement.isCompleted)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_achievement, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val achievement = achievements[position]
        holder.nameTextView.text = achievement.name
        holder.medalImageView.setImageResource(if (achievement.isCompleted) R.mipmap.ic_medalcomplete_foreground else R.mipmap.ic_medalnotcomplete_foreground)
    }

    override fun getItemCount(): Int {
        return achievements.size
    }

    fun countAchieved(): Int {
        return achievements.count { it.isCompleted }
    }
}

data class Achievement(val name: String, val description: String, val isCompleted: Boolean)