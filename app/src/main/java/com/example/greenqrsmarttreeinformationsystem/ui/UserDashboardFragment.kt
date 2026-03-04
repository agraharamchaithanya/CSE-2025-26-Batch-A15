package com.example.greenqrsmarttreeinformationsystem.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.greenqrsmarttreeinformationsystem.R
import com.example.greenqrsmarttreeinformationsystem.databinding.FragmentUserDashboardBinding
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult

class UserDashboardFragment : Fragment() {

    private var _binding: FragmentUserDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Improve camera performance
        binding.barcodeScannerView.setStatusText("")
        binding.barcodeScannerView.cameraSettings.isAutoFocusEnabled = true
        binding.barcodeScannerView.cameraSettings.requestedCameraId = 0

        // 🔥 Restrict scanner to QR only (VERY IMPORTANT)
        binding.barcodeScannerView.barcodeView.decoderFactory =
            com.journeyapps.barcodescanner.DefaultDecoderFactory(
                listOf(com.google.zxing.BarcodeFormat.QR_CODE)
            )
    }

    override fun onResume() {
        super.onResume()

        binding.barcodeScannerView.resume()

        binding.barcodeScannerView.decodeSingle(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {

                binding.barcodeScannerView.pause()   // 🔥 STOP CAMERA FAST

                result?.text?.let { qrText ->
                    handleQrCode(qrText)
                }
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint?>?) {}
        })
    }

    private fun handleQrCode(qrText: String) {
        if (qrText.startsWith("TREE_ID:")) {
            val treeId = qrText.removePrefix("TREE_ID:").toIntOrNull()
            if (treeId != null) {
                val bundle = Bundle().apply {
                    putInt("treeId", treeId)
                    putBoolean("fromScan", true)
                }

                findNavController().navigate(
                    R.id.action_userDashboardFragment_to_treeDetailsFragment,
                    bundle
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.barcodeScannerView.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}