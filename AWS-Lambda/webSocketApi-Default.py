import boto3
import json
import os
from botocore.exceptions import ClientError

dynamodb = boto3.resource('dynamodb')
table = dynamodb.Table('users')
api_gateway_management_api = boto3.client('apigatewaymanagementapi',
                                          endpoint_url="https://3tf96qjpy8.execute-api.eu-west-3.amazonaws.com/PolyChat")


def lambda_handler(event, context):
    connection_id = event['requestContext']['connectionId']
    body = json.loads(event['body'])
    action = body['action']
    data = body['data']

    if action == "connect":
        if 'user_id' in data:
            user_id = data['user_id']
            response = table.get_item(Key={'user_id': user_id})
            if 'Item' in response:
                existing_item = response['Item']
                if 'connection_id' not in response['Item']:
                    existing_item['connection_id'] = connection_id
                    table.put_item(Item=existing_item)
            else:
                print(f"No se encontró el user_id: {user_id}")

    elif action == "sendMessage":
        if 'userIds' in data and 'message' in data:
            userIds = data['userIds']
            message = {
                'action': action,
                'message': data['message']
            }
            connectionIds = []

            for userId in userIds:
                response = table.get_item(Key={'user_id': userId})
                if 'Item' in response and 'connection_id' in response['Item']:
                    connectionIds.append(response['Item']['connection_id'])

            for connectionId in connectionIds:
                try:
                    api_gateway_management_api.post_to_connection(
                        Data=json.dumps(message),
                        ConnectionId=connectionId
                    )
                except ClientError as e:
                    if e.response['Error']['Code'] == 'GoneException':
                        update_response = table.update_item(
                            Key={'user_id': userId},
                            UpdateExpression="remove connection_id",
                            ReturnValues="UPDATED_NEW"
                        )
                    else:
                        raise e

    elif action == "updateContacts":
        if 'user_id' in data and 'status' in data:
            user_id = data['user_id']

            response = table.get_item(Key={'user_id': user_id})

            if 'Item' in response and 'contactos' in response['Item']:
                contacts = response['Item']['contactos']

                for contact in contacts:
                    contact_response = table.get_item(Key={'user_id': contact})

                    if 'Item' in contact_response and 'connection_id' in contact_response['Item']:
                        try:
                            message = {
                                'action': action,
                                'message': f"El estado de {user_id} ha cambiado de status."
                            }
                            api_gateway_management_api.post_to_connection(
                                Data=message,
                                ConnectionId=contact_response['Item']['connection_id']
                            )
                        except ClientError as e:
                            if e.response['Error']['Code'] == 'GoneException':
                                update_response = table.update_item(
                                    Key={'user_id': contact},
                                    UpdateExpression="remove connection_id",
                                    ReturnValues="UPDATED_NEW"
                                )
                            else:
                                raise e
            else:
                print(f"No se encontraron contactos para el user_id: {user_id}")

    return {'statusCode': 200}
