import json
import boto3

dynamodb = boto3.resource('dynamodb')
table = dynamodb.Table('messages')

def lambda_handler(event, context):
    # Verificar si se proporciona pathParameters y message_id
    if 'message_id' not in event['application/json']:
        return {
            'statusCode': 400,
            'body': json.dumps({'error': 'Invalid request. Missing message_id'}),
            'event': event
        }

    # Obtener el message_id de los pathParameters
    message_id = event['application/json']['message_id']

    # Verificar si el mensaje existe en la tabla
    response = table.get_item(Key={'message_id': message_id})
    if 'Item' not in response:
        return {
            'statusCode': 404,
            'body': json.dumps({'error': 'Message not found'})
        }

    # Eliminar el mensaje de la tabla
    table.delete_item(Key={'message_id': message_id})

    return {
        'statusCode': 200,
        'body': json.dumps({'message': 'Message deleted successfully'})
    }
