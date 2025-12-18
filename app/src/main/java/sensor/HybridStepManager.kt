package com.example.stepdrink.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ImprovedStepManager - Fixed version untuk Samsung dan device lain
 *
 * Improvements:
 * - Better sensor detection
 * - Automatic fallback
 * - More logging
 * - Samsung compatibility fixes
 */
class ImprovedStepManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    // Try both sensors
    private val stepCounterSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val stepDetectorSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    // Flow states
    private val _totalSteps = MutableStateFlow(0)
    val totalSteps: StateFlow<Int> = _totalSteps.asStateFlow()

    private val _stepDetected = MutableStateFlow(false)
    val stepDetected: StateFlow<Boolean> = _stepDetected.asStateFlow()

    private val _isWalking = MutableStateFlow(false)
    val isWalking: StateFlow<Boolean> = _isWalking.asStateFlow()

    private val _sensorStatus = MutableStateFlow("")
    val sensorStatus: StateFlow<String> = _sensorStatus.asStateFlow()

    // Internal state
    private var baselineSteps = -1  // Changed to -1 for better initial detection
    private var sessionSteps = 0
    private var detectorStepCount = 0
    private var lastStepTime = 0L
    private var isTracking = false

    // Sensor availability flags
    private var hasStepCounter = false
    private var hasStepDetector = false

    companion object {
        private const val TAG = "ImprovedStepManager"
        private const val WALKING_TIMEOUT = 2000L
    }

    init {
        checkSensorAvailability()
    }

    private fun checkSensorAvailability() {
        hasStepCounter = stepCounterSensor != null
        hasStepDetector = stepDetectorSensor != null

        Log.d(TAG, "=== SENSOR CHECK ===")
        Log.d(TAG, "STEP_COUNTER available: $hasStepCounter")
        Log.d(TAG, "STEP_DETECTOR available: $hasStepDetector")

        if (hasStepCounter) {
            Log.d(TAG, "STEP_COUNTER: ${stepCounterSensor?.name} (${stepCounterSensor?.vendor})")
        }
        if (hasStepDetector) {
            Log.d(TAG, "STEP_DETECTOR: ${stepDetectorSensor?.name} (${stepDetectorSensor?.vendor})")
        }

        updateSensorStatus()
    }

    private fun updateSensorStatus() {
        _sensorStatus.value = when {
            hasStepCounter && hasStepDetector -> "Hybrid Mode (Counter + Detector)"
            hasStepDetector -> "Detector Only Mode"
            hasStepCounter -> "Counter Only Mode"
            else -> "No Sensor Available"
        }
    }

    fun isSensorAvailable(): Boolean {
        return hasStepCounter || hasStepDetector
    }

    fun startTracking() {
        if (isTracking) {
            Log.w(TAG, "Already tracking!")
            return
        }

        Log.d(TAG, "=== START TRACKING ===")
        isTracking = true

        var sensorsRegistered = 0

        // Register STEP_DETECTOR first (priority for responsiveness)
        if (hasStepDetector) {
            val success = sensorManager.registerListener(
                this,
                stepDetectorSensor,
                SensorManager.SENSOR_DELAY_FASTEST
            )
            Log.d(TAG, "STEP_DETECTOR registration: ${if (success) "SUCCESS" else "FAILED"}")
            if (success) sensorsRegistered++
        }

        // Register STEP_COUNTER for accuracy
        if (hasStepCounter) {
            val success = sensorManager.registerListener(
                this,
                stepCounterSensor,
                SensorManager.SENSOR_DELAY_UI
            )
            Log.d(TAG, "STEP_COUNTER registration: ${if (success) "SUCCESS" else "FAILED"}")
            if (success) sensorsRegistered++
        }

        Log.d(TAG, "Total sensors registered: $sensorsRegistered")

        if (sensorsRegistered == 0) {
            Log.e(TAG, "NO SENSORS REGISTERED!")
            isTracking = false
        }
    }

    fun stopTracking() {
        if (!isTracking) return

        Log.d(TAG, "=== STOP TRACKING ===")
        sensorManager.unregisterListener(this)
        isTracking = false
        _isWalking.value = false
    }

    fun resetSteps() {
        Log.d(TAG, "=== RESET STEPS ===")
        baselineSteps = -1
        sessionSteps = 0
        detectorStepCount = 0
        _totalSteps.value = 0
        lastStepTime = 0L
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!isTracking) return

        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_STEP_COUNTER -> handleStepCounter(it)
                Sensor.TYPE_STEP_DETECTOR -> handleStepDetector(it)
            }
        }
    }

    private fun handleStepCounter(event: SensorEvent) {
        val rawSteps = event.values[0].toInt()

        if (baselineSteps == -1) {
            // First reading - set baseline
            baselineSteps = rawSteps
            Log.d(TAG, "STEP_COUNTER: Baseline set to $baselineSteps")

            // If we have detector, use its count
            if (hasStepDetector && detectorStepCount > 0) {
                sessionSteps = detectorStepCount
                Log.d(TAG, "STEP_COUNTER: Using detector count $detectorStepCount")
            } else {
                sessionSteps = 0
            }
        } else {
            // Calculate session steps
            val counterSessionSteps = rawSteps - baselineSteps

            // If we have detector, use detector count (more accurate for short sessions)
            // Otherwise use counter
            if (hasStepDetector && detectorStepCount > 0) {
                sessionSteps = detectorStepCount
                Log.d(TAG, "STEP_COUNTER: Raw=$rawSteps, Counter=${counterSessionSteps}, Using Detector=$detectorStepCount")
            } else {
                sessionSteps = counterSessionSteps
                Log.d(TAG, "STEP_COUNTER: Raw=$rawSteps, Session=$sessionSteps")
            }
        }

        _totalSteps.value = sessionSteps
    }

    private fun handleStepDetector(event: SensorEvent) {
        val currentTime = System.currentTimeMillis()

        // Increment counter
        detectorStepCount++
        sessionSteps = detectorStepCount

        // Update total immediately
        _totalSteps.value = sessionSteps

        // Update walking state
        _isWalking.value = true
        lastStepTime = currentTime

        // Trigger visual feedback
        _stepDetected.value = true

        Log.d(TAG, "STEP_DETECTOR: Step #$detectorStepCount detected!")

        // Reset visual feedback after animation
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            _stepDetected.value = false
        }, 200)

        // Schedule walking timeout check
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            checkWalkingTimeout(currentTime)
        }, WALKING_TIMEOUT)
    }

    private fun checkWalkingTimeout(stepTime: Long) {
        if (stepTime == lastStepTime) {
            val timeSinceLastStep = System.currentTimeMillis() - lastStepTime
            if (timeSinceLastStep >= WALKING_TIMEOUT) {
                _isWalking.value = false
                Log.d(TAG, "Walking stopped (timeout)")
            }
        }
    }

    fun getDebugInfo(): String {
        return """
            Sensor Status: ${_sensorStatus.value}
            Tracking: $isTracking
            Total Steps: ${_totalSteps.value}
            Detector Count: $detectorStepCount
            Counter Baseline: $baselineSteps
            Walking: ${_isWalking.value}
        """.trimIndent()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        val accuracyStr = when (accuracy) {
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> "HIGH"
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> "MEDIUM"
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> "LOW"
            SensorManager.SENSOR_STATUS_NO_CONTACT -> "NO_CONTACT"
            SensorManager.SENSOR_STATUS_UNRELIABLE -> "UNRELIABLE"
            else -> "UNKNOWN"
        }
        Log.d(TAG, "Sensor accuracy changed: ${sensor?.name} = $accuracyStr")
    }
}