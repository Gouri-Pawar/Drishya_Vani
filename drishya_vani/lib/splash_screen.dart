// lib/splash_screen.dart
// Splash screen with simple fade-in animation and auto-navigation to HomeScreen

import 'package:flutter/material.dart';

/// Simple SplashScreen widget for Drishya Vani.
///
/// Usage:
/// 1. Put your app logo at `assets/logo.png` and list it in pubspec.yaml:
///
///    flutter:
///      assets:
///        - assets/logo.png
///
/// 2. Either set this widget as the home in your `main.dart` or navigate to it:
///
///    void main() => runApp(const MaterialApp(home: SplashScreen()));
///
/// 3. The splash screen automatically navigates to a placeholder HomeScreen
///    after the configured duration. Replace `HomeScreen` with your actual
///    onboarding/home widget later.

class SplashScreen extends StatefulWidget {
  /// How long the splash remains visible (default 2 seconds)
  final Duration duration;
  const SplashScreen({Key? key, this.duration = const Duration(seconds: 2)}) : super(key: key);

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> with SingleTickerProviderStateMixin {
  late final AnimationController _controller;
  late final Animation<double> _fadeAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(vsync: this, duration: const Duration(milliseconds: 700));
    _fadeAnimation = CurvedAnimation(parent: _controller, curve: Curves.easeIn);
    _controller.forward();

    // After the requested duration, navigate to the next screen.
    Future.delayed(widget.duration, () {
      if (!mounted) return;
      Navigator.of(context).pushReplacement(
        MaterialPageRoute(builder: (_) => const HomeScreen()),
      );
    });
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Scaffold(
      body: Container(
        width: double.infinity,
        height: double.infinity,
        color: theme.scaffoldBackgroundColor,
        child: FadeTransition(
          opacity: _fadeAnimation,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const SizedBox(height: 80),

              // Logo: replace assets/logo.png with your actual logo image
              Image.asset('assets/logo.png', width: 140, height: 140),

              const SizedBox(height: 24),

              Text(
                'Drishya Vani',
                style: theme.textTheme.titleLarge?.copyWith(fontSize: 22, fontWeight: FontWeight.bold),
              ),

              const SizedBox(height: 8),

              Text(
                'Listen to your journey',
                style: theme.textTheme.bodyMedium,
              ),
            ],
          ),
        ),
      ),
    );
  }
}

/// A small placeholder HomeScreen so the splash can navigate without errors.
/// Replace this with your real Home/Onboarding screen later.
class HomeScreen extends StatelessWidget {
  const HomeScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Drishya Vani')),
      body: const Center(child: Text('Home Screen - replace me')),
    );
  }
}
