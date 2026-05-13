import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class ApiService {
  // For mobile: Use PC's IP address
  // For web: Use 'localhost'
  static const String baseUrl = 'http://20.30.42.246:8080/api';
  final Dio _dio = Dio();
  final _storage = const FlutterSecureStorage();

  ApiService() {
    _dio.options.baseUrl = baseUrl;
    _dio.interceptors.add(
      InterceptorsWrapper(
        onRequest: (options, handler) async {
          final token = await _storage.read(key: 'auth_token');
          if (token != null) {
            options.headers['Authorization'] = 'Bearer $token';
          }
          return handler.next(options);
        },
      ),
    );
  }

  Future<Map<String, dynamic>> login(String username, String email, String password) async {
    try {
      final response = await _dio.post('/auth/login', data: {
        'username': username,
        'email': email,
        'password': password,
      });
      
      if (response.data['success']) {
        await _storage.write(key: 'auth_token', value: response.data['data']['token']);
        await _storage.write(key: 'user_role', value: response.data['data']['role']);
      }
      
      return response.data;
    } catch (e) {
      return {'success': false, 'message': 'Login failed: $e'};
    }
  }

  Future<Map<String, dynamic>> register(String username, String email, String password) async {
    try {
      final response = await _dio.post('/auth/register', data: {
        'username': username,
        'email': email,
        'password': password,
      });
      
      if (response.data['success']) {
        await _storage.write(key: 'auth_token', value: response.data['data']['token']);
        await _storage.write(key: 'user_role', value: response.data['data']['role']);
      }
      
      return response.data;
    } catch (e) {
      return {'success': false, 'message': 'Registration failed: $e'};
    }
  }

  Future<void> logout() async {
    await _storage.delete(key: 'auth_token');
    await _storage.delete(key: 'user_role');
  }

  Future<String?> getToken() async {
    return await _storage.read(key: 'auth_token');
  }

  Future<String?> getUserRole() async {
    return await _storage.read(key: 'user_role');
  }

  // Session APIs
  Future<Map<String, dynamic>> startSession() async {
    try {
      final response = await _dio.post('/sessions/start');
      return response.data;
    } catch (e) {
      return {'success': false, 'message': 'Failed to start session: $e'};
    }
  }

  Future<Map<String, dynamic>> updateLocation(String sessionId, double lat, double lng) async {
    try {
      final response = await _dio.post('/sessions/$sessionId/location', data: {
        'lat': lat,
        'lng': lng,
      });
      return response.data;
    } catch (e) {
      return {'success': false, 'message': 'Failed to update location: $e'};
    }
  }

  Future<Map<String, dynamic>> addCheckpoint(String sessionId, double lat, double lng, String? notes) async {
    try {
      final response = await _dio.post('/sessions/$sessionId/checkpoint', data: {
        'lat': lat,
        'lng': lng,
        'notes': notes,
      });
      return response.data;
    } catch (e) {
      return {'success': false, 'message': 'Failed to add checkpoint: $e'};
    }
  }

  Future<Map<String, dynamic>> endSession(String sessionId) async {
    try {
      final response = await _dio.post('/sessions/$sessionId/end');
      return response.data;
    } catch (e) {
      return {'success': false, 'message': 'Failed to end session: $e'};
    }
  }

  Future<List<Map<String, dynamic>>> getUserSessions() async {
    try {
      final response = await _dio.get('/sessions/user');
      if (response.data['success']) {
        return List<Map<String, dynamic>>.from(response.data['data']);
      }
      return [];
    } catch (e) {
      return [];
    }
  }

  Future<Map<String, dynamic>> getSessionDetails(String sessionId) async {
    try {
      final response = await _dio.get('/sessions/$sessionId');
      return response.data;
    } catch (e) {
      return {'success': false, 'message': 'Failed to get session details: $e'};
    }
  }

  // Admin APIs
  Future<Map<String, dynamic>> getAdminStats() async {
    try {
      final response = await _dio.get('/admin/stats');
      if (response.data['success']) {
        return response.data['data'];
      }
      return {};
    } catch (e) {
      return {};
    }
  }

  Future<List<Map<String, dynamic>>> getAdminUsers() async {
    try {
      final response = await _dio.get('/admin/users');
      if (response.data['success']) {
        return List<Map<String, dynamic>>.from(response.data['data']);
      }
      return [];
    } catch (e) {
      return [];
    }
  }

  Future<List<Map<String, dynamic>>> getAllSessions() async {
    try {
      final response = await _dio.get('/admin/sessions/all');
      if (response.data['success']) {
        return List<Map<String, dynamic>>.from(response.data['data']);
      }
      return [];
    } catch (e) {
      return [];
    }
  }

  Future<List<int>> downloadExportAllUsers() async {
    try {
      final response = await _dio.get(
        '/admin/export/all-users',
        options: Options(responseType: ResponseType.bytes),
      );
      return response.data;
    } catch (e) {
      throw Exception('Failed to download export: $e');
    }
  }

  Future<List<int>> downloadExportUser(String userId) async {
    try {
      final response = await _dio.get(
        '/admin/export/user/$userId',
        options: Options(responseType: ResponseType.bytes),
      );
      return response.data;
    } catch (e) {
      throw Exception('Failed to download export: $e');
    }
  }

  // User Management APIs
  Future<Map<String, dynamic>> addUser(Map<String, dynamic> userData) async {
    try {
      final response = await _dio.post('/auth/register', data: userData);
      return response.data;
    } catch (e) {
      return {'success': false, 'message': 'Failed to add user: $e'};
    }
  }

  Future<Map<String, dynamic>> bulkImportUsers(List<Map<String, dynamic>> users) async {
    try {
      final response = await _dio.post('/auth/bulk-import', data: {'users': users});
      return response.data;
    } catch (e) {
      return {'success': false, 'message': 'Failed to import users: $e'};
    }
  }

  Future<List<Map<String, dynamic>>> getAllUsers() async {
    try {
      final response = await _dio.get('/auth/users');
      if (response.data['success']) {
        return List<Map<String, dynamic>>.from(response.data['data']);
      }
      return [];
    } catch (e) {
      return [];
    }
  }

  Future<Map<String, dynamic>> deleteUser(String username) async {
    try {
      final response = await _dio.delete('/auth/users/$username');
      return response.data;
    } catch (e) {
      return {'success': false, 'message': 'Failed to delete user: $e'};
    }
  }

  Future<Map<String, dynamic>> deleteMultipleUsers(List<String> usernames) async {
    try {
      final response = await _dio.post('/auth/users/delete-multiple', data: {
        'usernames': usernames,
      });
      return response.data;
    } catch (e) {
      return {'success': false, 'message': 'Failed to delete users: $e'};
    }
  }
}
